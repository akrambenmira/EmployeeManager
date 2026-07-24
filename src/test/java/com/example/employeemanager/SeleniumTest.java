package com.example.employeemanager;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class SeleniumTest {

    WebDriver driver;

    @BeforeEach
    void setup() {

        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();

        driver.manage().window().maximize();

        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterEach
    void close() {
        driver.quit();
    }

    @Test
    void employeeWorkflow() {

        driver.get("http://localhost:8080");

        // Home page
        driver.findElement(By.linkText("Manage Employees")).click();

        // Employees page
        driver.findElement(By.linkText("Add Employee")).click();

        // Employee form
        driver.findElement(By.id("firstName"))
                .sendKeys("John");

        driver.findElement(By.id("lastName"))
                .sendKeys("Doe");

        driver.findElement(By.id("email"))
                .sendKeys("john@test.com");

        driver.findElement(By.id("position"))
                .sendKeys("Developer");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Verify employee exists
        assertTrue(driver.getPageSource().contains("John"));

        // Delete employee
        driver.findElement(By.linkText("Delete")).click();

        driver.switchTo().alert().accept();

        // Verify employee removed
        assertFalse(driver.getPageSource().contains("John"));
    }
}