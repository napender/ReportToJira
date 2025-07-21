package com.reporttojira;

import com.google.gson.Gson;
import okhttp3.*;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class JiraClient {
    private static final Properties config = new Properties();
    private static final Gson gson = new Gson();

    /**
     * This block loads the Jira connection details from the `config.properties` file.
     * It runs once when the class is first loaded.
     */
    static {
        try (var inputStream = JiraClient.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new IOException("config.properties not found in classpath");
            }
            config.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            // Consider a more robust error handling mechanism, like logging or exiting
        }
    }

    /**
     * Creates the Base64 encoded authentication header required by the Jira API.
     * @return The "Basic" authentication string.
     * @throws IllegalStateException if apiEmail or apiToken are missing from the config.
     */
    private static String getBasicAuthHeader() {
        String email = config.getProperty("apiEmail");
        String token = config.getProperty("apiToken");
        if (email == null || token == null || email.isEmpty() || token.isEmpty()) {
            throw new IllegalStateException("apiEmail and apiToken must be set in config.properties");
        }
        String credentials = email + ":" + token;
        return "Basic " + java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    /**
     * Creates a new ticket in Jira using the data parsed from the report.
     * @param data The ReportData object containing the ticket title, description, etc.
     * @return The ID of the newly created Jira ticket (e.g., "PRH-82").
     * @throws Exception if the ticket creation or attachment upload fails.
     */
    public static String createTicket(ReportData data) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // 1. Create the request object from the report data
        JiraTicketRequest ticketRequest = new JiraTicketRequest(data, config);

        // 2. Convert the request object into a JSON string
        String json = gson.toJson(ticketRequest);

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(config.getProperty("jiraUrl") + "/rest/api/2/issue")
                .post(body)
                .addHeader("Authorization", getBasicAuthHeader())
                .build();

        // Execute the request and handle the response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Failed to create ticket: " + response.code() + " " + response.message());
                if (response.body() != null) {
                    System.err.println("Response body: " + response.body().string());
                }
                throw new IOException("Failed to create Jira ticket. Response code: " + response.code());
            }

            // Extract the ticket ID from the successful response
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            String ticketId = jsonResponse.getString("key");
            System.out.println("Successfully created ticket: " + ticketId);

            // If a screenshot exists, attach it to the new ticket
            if (data.getScreenshotPath() != null && !data.getScreenshotPath().isEmpty()) {
                attachScreenshot(client, ticketId, data.getScreenshotPath());
            }
            return ticketId;
        }
    }

    /**
     * Attaches a file (screenshot) to an existing Jira ticket.
     * @param client The HTTP client to use for the request.
     * @param ticketId The ID of the ticket to attach the file to (e.g., "PRH-82").
     * @param screenshotPath The absolute local path to the screenshot file.
     * @throws IOException if the file cannot be read or the upload fails.
     */
    private static void attachScreenshot(OkHttpClient client, String ticketId, String screenshotPath) throws IOException {
        File screenshotFile = new File(screenshotPath);
        if (!screenshotFile.exists()) {
            System.err.println("Screenshot file not found: " + screenshotPath);
            return;
        }

        RequestBody attachBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", screenshotFile.getName(), RequestBody.create(screenshotFile, MediaType.get("image/png")))
                .build();

        Request attachReq = new Request.Builder()
                .url(config.getProperty("jiraUrl") + "/rest/api/2/issue/" + ticketId + "/attachments")
                .post(attachBody)
                .addHeader("X-Atlassian-Token", "no-check")
                .addHeader("Authorization", getBasicAuthHeader())
                .build();

        try (Response attachResponse = client.newCall(attachReq).execute()) {
            if (!attachResponse.isSuccessful()) {
                System.err.println("Failed to attach screenshot: " + attachResponse.code() + " " + attachResponse.message());
                if (attachResponse.body() != null) {
                    System.err.println(attachResponse.body().string());
                }
            } else {
                System.out.println("Screenshot attached successfully to " + ticketId);
            }
        }
    }
}
