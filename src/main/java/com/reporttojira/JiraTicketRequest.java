package com.reporttojira;

import java.util.Properties;

/**
 * Represents the JSON structure for a Jira issue creation request.
 * This class can be easily modified to add, remove, or change fields.
 */
public class JiraTicketRequest {

    private final Fields fields;

    /**
     * Constructs a new Jira ticket request.
     * @param reportData The data parsed from the test report.
     * @param config The application configuration properties.
     */
    public JiraTicketRequest(ReportData reportData, Properties config) {
        this.fields = new Fields(
                new Project(config.getProperty("projectKey")),
                reportData.getTitle(),
                reportData.getDescription(),
                new IssueType(config.getProperty("issueType", "Bug"))
        );
    }

    // --- Nested classes to match Jira's JSON structure ---

    private static class Fields {
        private final Project project;
        private final String summary;
        private final String description;
        private final IssueType issuetype;

        /**
         * Constructs the Fields object for the Jira ticket.
         * @param project The Jira project.
         * @param summary The summary or title of the ticket.
         * @param description The description of the ticket.
         * @param issuetype The type of the issue (e.g., Bug, Story).
         */
        public Fields(Project project, String summary, String description, IssueType issuetype) {
            this.project = project;
            this.summary = summary;
            this.description = description;
            this.issuetype = issuetype;
        }
    }

    private static class Project {
        private final String key;

        /**
         * Constructs a Project object.
         * @param key The key of the Jira project.
         */
        public Project(String key) {
            this.key = key;
        }
    }

    private static class IssueType {
        private final String name;

        /**
         * Constructs an IssueType object.
         * @param name The name of the issue type.
         */
        public IssueType(String name) {
            this.name = name;
        }
    }
}