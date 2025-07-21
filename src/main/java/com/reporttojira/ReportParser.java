package com.reporttojira;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class ReportParser {

    public static ReportData parse(String urlOrPath) throws IOException {
        String reportUrl;
        // Check if the input is a web URL or a local file path
        if (urlOrPath.toLowerCase().startsWith("http://") || urlOrPath.toLowerCase().startsWith("https://")) {
            reportUrl = urlOrPath;
            System.out.println("Parsing remote URL: " + reportUrl);
        } else {
            File reportFile = new File(urlOrPath);
            if (!reportFile.exists()) {
                throw new IOException("The specified file does not exist: " + reportFile.getAbsolutePath());
            }
            reportUrl = reportFile.toURI().toString();
            System.out.println("Parsing local file: " + reportUrl);
        }

        WebDriverManager.getInstance(DriverManagerType.CHROME).setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-gpu", "--disable-dev-shm-usage");
        WebDriver driver = new ChromeDriver(options);

        ReportData reportData = null;
        try {
            driver.get(reportUrl);

            // --- Extract data using new XPaths for dummy_report.html ---

            String testName = driver.findElement(By.xpath("//div[@class='test-name']")).getText();

            List<WebElement> envElements = driver.findElements(By.xpath("//div[@class='test-info']/span"));
            String env = envElements.stream()
                                    .map(WebElement::getText)
                                    .collect(Collectors.joining("\n"));

            List<WebElement> logElements = driver.findElements(By.xpath("//div[@class='test-steps']//tbody/tr"));
            String logs = logElements.stream()
                                     .map(row -> {
                                         String status = row.findElement(By.xpath("./td[1]")).getText();
                                         String details = row.findElement(By.xpath("./td[2]")).getText();
                                         return String.format("[%s] %s", status, details);
                                     })
                                     .collect(Collectors.joining("\n"));

            String screenshotDataUrl = driver.findElement(By.xpath("//img[@class='screenshot']")).getAttribute("src");
            String screenshotPath = saveScreenshotFromDataUrl(screenshotDataUrl, driver.getCurrentUrl());

            reportData = new ReportData(testName, env, logs, screenshotDataUrl, screenshotPath);

            System.out.println("Successfully parsed data from report:");
            System.out.println("  Title: " + reportData.getTitle());
            System.out.println("  Environment: " + reportData.getEnv());
            System.out.println("  Screenshot Path: " + reportData.getScreenshotPath());

        } finally {
            driver.quit();
        }
        return reportData;
    }

    private static String saveScreenshotFromDataUrl(String dataUrl, String reportUrl) throws IOException {
        if (dataUrl == null || dataUrl.isEmpty()) {
            System.err.println("Screenshot source is empty.");
            return null;
        }

        // Handle base64 encoded images
        if (dataUrl.startsWith("data:image/")) {
            System.out.println("Processing base64 screenshot...");
            String base64ImageData = dataUrl.substring(dataUrl.indexOf(',') + 1);
            byte[] imageBytes = Base64.getDecoder().decode(base64ImageData);

            File tempFile = File.createTempFile("screenshot-", ".png");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(imageBytes);
            }
            System.out.println("Saved base64 screenshot to: " + tempFile.getAbsolutePath());
            return tempFile.getAbsolutePath();
        }
        // Handle local file paths
        else if (dataUrl.startsWith("file://")) {
            System.out.println("Processing local file screenshot...");
            File sourceFile = new File(dataUrl.substring("file://".length()));
             if (sourceFile.exists()) {
                System.out.println("Found local screenshot file: " + sourceFile.getAbsolutePath());
                return sourceFile.getAbsolutePath();
            } else {
                 System.err.println("Screenshot file not found at: " + sourceFile.getAbsolutePath());
                 return null;
            }
        }
        // Handle relative paths (like 'generated_image.png')
        else {
            System.out.println("Processing relative path screenshot...");
            File imageFile = new File(dataUrl);
            if (imageFile.exists()) {
                System.out.println("Found relative screenshot file: " + imageFile.getAbsolutePath());
                return imageFile.getAbsolutePath();
            } else {
                System.err.println("Relative screenshot file not found: " + imageFile.getAbsolutePath());
                return null;
            }
        }
    }
}
