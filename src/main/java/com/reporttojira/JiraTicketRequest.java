package com.reporttojira;

import java.util.Properties;

/**
 * Represents the JSON structure for a Jira issue creation request.
 * This class can be easily modified to add, remove, or change fields.
 */
public class JiraTicketRequest {

    private final Fields fields;

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

        public Fields(Project project, String summary, String description, IssueType issuetype) {
            this.project = project;
            this.summary = summary;
            this.description = description;
            this.issuetype = issuetype;
        }
    }

    private static class Project {
        private final String key;

        public Project(String key) {
            this.key = key;
        }
    }

    private static class IssueType {
        private final String name;

        public IssueType(String name) {
            this.name = name;
        }
    }
}
