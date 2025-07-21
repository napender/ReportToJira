package com.reporttojira;

/**
 * A simple data class to hold all the information extracted from the test report.
 * This object is then used to create the Jira ticket.
 */
public class ReportData {
    private final String testName;
    private final String env;
    private final String logs;
    private final String screenshotUrl;
    private final String screenshotPath;

    /**
     * Constructor to create a new ReportData object.
     * @param testName The name of the test, used as the Jira ticket title.
     * @param env The environment details (e.g., OS, Browser).
     * @param logs The execution logs or steps from the test.
     * @param screenshotUrl The original URL or source of the screenshot.
     * @param screenshotPath The local file path where the screenshot is saved.
     */
    public ReportData(String testName, String env, String logs, String screenshotUrl, String screenshotPath) {
        this.testName = testName;
        this.env = env;
        this.logs = logs;
        this.screenshotUrl = screenshotUrl;
        this.screenshotPath = screenshotPath;
    }

    // --- Getters for the fields ---

    public String getTestName() {
        return testName;
    }

    public String getEnv() {
        return env;
    }

    public String getLogs() {
        return logs;
    }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    public String getScreenshotPath() {
        return screenshotPath;
    }

    /**
     * Gets the title for the Jira ticket.
     * @return The test name.
     */
    public String getTitle() {
        return testName;
    }

    /**
     * Constructs the description for the Jira ticket from the report data.
     * @return A formatted string containing the environment and log details.
     */
    public String getDescription() {
        return "Environment Details:\n" + env + "\n\nExecution Logs:\n" + logs;
    }
}
