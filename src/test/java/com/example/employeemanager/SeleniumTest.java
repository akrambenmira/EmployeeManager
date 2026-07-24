package com.example.employeemanager;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SeleniumTest {

    private WebDriver driver;
    private String baseUrl;
    private WebDriverWait wait;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        // Setup WebDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();

        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        driver = new ChromeDriver(options);
        baseUrl = "http://localhost:" + port;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testEmployeeManagementWorkflow() {
        // Step 1: Navigate to the employee list page
        driver.get(baseUrl + "/employees");

        // Verify we're on the employees page
        WebElement pageTitle = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.tagName("h2"))
        );
        assertTrue(pageTitle.getText().contains("Employee Management System"),
                "Employee Management System page should be displayed");

        // Step 2: Click "Add Employee" button
        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Add Employee"))
        );
        addButton.click();

        // Wait for form to load by checking for the form element (wait for any form)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("form")));

        // Step 3: Fill in employee form
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String position = "Software Engineer";

        // Fill firstName
        WebElement firstNameInput = driver.findElement(By.name("firstName"));
        firstNameInput.clear();
        firstNameInput.sendKeys(firstName);

        // Fill lastName
        WebElement lastNameInput = driver.findElement(By.name("lastName"));
        lastNameInput.clear();
        lastNameInput.sendKeys(lastName);

        // Fill email
        WebElement emailInput = driver.findElement(By.name("email"));
        emailInput.clear();
        emailInput.sendKeys(email);

        // Fill position
        WebElement positionInput = driver.findElement(By.name("position"));
        positionInput.clear();
        positionInput.sendKeys(position);

        // Step 4: Submit the form
        WebElement saveButton = driver.findElement(By.xpath("//button[@type='submit']"));
        saveButton.click();

        // Step 5: Verify employee appears in the list
        wait.until(ExpectedConditions.urlContains("/employees"));

        // Wait for the table to update
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("tbody")));

        // Find the row containing the employee by looking for First Name in a table cell
        WebElement tableBody = driver.findElement(By.tagName("tbody"));

        // Check if employee row exists (simple approach: check if text appears in page)
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(firstName),
                "First name should appear in the list");
        assertTrue(pageSource.contains(lastName),
                "Last name should appear in the list");
        assertTrue(pageSource.contains(email),
                "Email should appear in the list");

        // Step 6: Delete the employee
        // Find all delete buttons and click the one for our employee
        // The delete button is in the same row as our employee
        try {
            // Wait a bit for the page to fully render
            Thread.sleep(500);

            // Find the delete button by locating the row with our employee data
            // Then find the delete button in that row
            WebElement deleteButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//tr[contains(., '" + firstName + "')]//a[@class='btn btn-danger btn-sm']")
                    )
            );

            deleteButton.click();

            // Handle the confirmation alert
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();

        } catch (Exception e) {
            // If the above doesn't work, try a different approach
            // Get all delete buttons and click the last one
            java.util.List<WebElement> deleteButtons = driver.findElements(
                    By.xpath("//a[@class='btn btn-danger btn-sm']")
            );
            if (!deleteButtons.isEmpty()) {
                deleteButtons.get(0).click();
                try {
                    wait.until(ExpectedConditions.alertIsPresent());
                    driver.switchTo().alert().accept();
                } catch (Exception ignored) {}
            }
        }

        // Step 7: Verify employee is deleted
        wait.until(ExpectedConditions.urlContains("/employees"));

        // Wait a moment for the table to update
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check if employee no longer appears
        String updatedPageSource = driver.getPageSource();

        // The employee should not be in the page or only appear once (in history/logs if any)
        // Count occurrences - should be 0 or minimal
        int occurrences = countOccurrences(updatedPageSource, firstName);
        assertTrue(occurrences == 0,
                "Employee should be deleted and not appear in the list. Found " + occurrences + " occurrence(s)");
    }

    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}