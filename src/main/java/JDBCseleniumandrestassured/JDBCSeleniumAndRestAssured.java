package JDBCseleniumandrestassured;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class JDBCSeleniumAndRestAssured {
	@Test
	public static void logIn() throws InterruptedException, SQLException {
		System.setProperty("webdriver.chrome.driver","C:/Users/sabah/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
	     options.addArguments("--remote-allow-origins=*");
		WebDriver driver = new ChromeDriver();
		driver.get("http://49.249.28.218:8091/dashboard/projects");
		WebElement userName = driver.findElement(By.id("username"));
		WebElement passWord = driver.findElement(By.id("inputPassword"));
		userName.sendKeys("rmgyantra");
		passWord.sendKeys("rmgy@9999");
		//Thread.sleep(2000);
		WebElement logIn = driver.findElement(By.xpath("//button[1]"));
		logIn.click();
		Thread.sleep(5000);
		WebElement project = driver.findElement(By.xpath("//a[text()='Projects']"));
		project.click();
		
		//create a new project
		/**
		WebElement createProject= driver.findElement(By.xpath("//button[@class='btn btn-success']"));
		createProject.click();
		WebElement projectName= driver.findElement(By.xpath("//input[@name='projectName']"));
		projectName.sendKeys("DBProject");
		
//		WebElement teamSize= driver.findElement(By.xpath("//input[@name='teamSize']"));
//		teamSize.sendKeys("7");
		
		WebElement projectManger= driver.findElement(By.xpath("//input[@name='createdBy']"));
		projectManger.sendKeys("Ban N");
		
		WebElement projectStatus= driver.findElement(By.xpath("//div[3]/div/div/form/div[2]/div[4]/select"));
		//projectStatus.click();
		Select select = new Select(projectStatus);
		select.selectByValue("Created");
		
		WebElement addProject = driver.findElement(By.xpath("//input[@value='Add Project']"));
		addProject.click();*/
		
	//}
	
	//public static void connectWJDBC() throws SQLException {
		Connection con = null;
		String myProject ="DBProject";
		boolean isFound=false;
		try {
			con = DriverManager.getConnection("jdbc:mysql://49.249.28.218:3307/ninza_hrm","root@%","root");
			Statement stat = con.createStatement();
			String query= "Select * from Project";
			ResultSet result= stat.executeQuery(query);
			while(result.next()) {
				String MyprojectName=result.getNString("project_name");
//				System.out.println(result.getString(1)+"\t"
//			+ result.getString(2)
//			+"\t"+result.getString(3)
//			+"\t"+result.getString(4)
//			+"\t"+result.getString(5));
				if(MyprojectName.equalsIgnoreCase(myProject)) {
					isFound=true;
					System.out.println("the project is found:   "+ MyprojectName);
				}
			}
			if(isFound)
				Assert.assertTrue(isFound, "The project is found" );
			else {
				System.out.println("The project is not found");
				Assert.fail();
			}
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("The project do not exist");
		} finally {
			con.close();
		}
		
	}
	@Test
	public static void DBwithRestAssured() {
		RestAssured.baseURI="http://49.249.28.218:8091";
		String myProject="DBProject";
		boolean isFound=false;
		Response response= RestAssured
				.given()
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.get("/projects");
		//System.out.println(response.asPrettyString());
		
		int actualStatusCode= response.getStatusCode();
		Assert.assertEquals(actualStatusCode, 200);
		JsonPath jsonPath= response.jsonPath();
		List<Map<String,Object>> projects= jsonPath.getList("$"); // to start from the root of Json response
		for(Map<String, Object> project:projects) {
			String projectName= (String) project.get("projectName");
			if(projectName.equalsIgnoreCase(myProject)) {
				System.out.println("project is found in th DB with rest assured test");
				Assert.assertTrue(true, "project is found in the DB with rest assured test");
				isFound=true;
				break;
			}
			
		}
		if(!isFound) {
			System.out.println("project is not found in the DB with rest assured test");
			Assert.fail("project is not found in the DB with rest assured test");
		}
		
	}


}
