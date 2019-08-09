package com.group.cll;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);

		// 如果不设置将搜索环境变量
		System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
		WebDriver localDriver = new ChromeDriver();
		localDriver.get("http://localhost:8080/");
	}
}
