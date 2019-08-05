package cn.xsshome.selenium;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestOCR {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String baseUrl = "http://www.baidu.com/";

		WebDriver driver = new ChromeDriver();

		driver.get(baseUrl);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.open()");

		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());

		driver.switchTo().window(tabs.get(1)); // switches to new tab

		driver.get("http://www.sohu.com");

	}
}