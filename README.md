# Report to Jira: Your Friendly Neighborhood Ticket Creator 🤖

Tired of manually creating Jira tickets from your test reports? Do you find yourself copy-pasting, and then copy-pasting again, until your fingers go numb? Well, suffer no more! This tool automates the boring stuff, so you can get back to the important things, like drinking coffee and pretending to be busy.

## What Problem Does It Solve? 🤔

This project is a lifesaver for anyone who needs to create Jira tickets from HTML test reports. It parses the report, extracts the important details (like the test name, environment, and logs), and even grabs a screenshot to create a new ticket in your Jira project. It's like having a personal assistant who never complains or asks for a raise.

## Project Setup Requirements 🛠️

Before you can unleash the magic, you'll need a few things:

*   **Java (JDK 17 or higher):** Because this project is powered by the nectar of the code gods.
*   **Maven:** To build the project and manage dependencies. It's like a chef for your code, but without the yelling.
*   **Google Chrome:** The tool uses ChromeDriver to parse the HTML report, so you'll need Chrome installed.

## Step-by-Step Setup 🚀

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/ReportToJira.git
    cd ReportToJira
    ```

2.  **Install dependencies:**
    ```bash
    mvn clean install
    ```

## Configuration: The Secret Sauce 🌶️

To connect to your Jira instance, you need to create a `config.properties` file in the `src/main/resources` directory. You can copy the `sample_config.properties` file to get started.

```properties
jiraUrl=https://your-domain.atlassian.net
apiEmail=your-email@example.com
apiToken=your-jira-api-token
projectKey=YOUR_PROJECT_KEY
issueType=Story
```

*   `jiraUrl`: The URL of your Jira instance.
*   `apiEmail`: The email you use to log in to Jira.
*   `apiToken`: Your Jira API token. You can generate one [here](https://id.atlassian.com/manage-profile/security/api-tokens).
*   `projectKey`: The key of your Jira project (e.g., "PROJ").
*   `issueType`: The type of issue to create (e.g., "Bug", "Story", "Task").

## How to Build the JAR 📦

To build the executable JAR file, run the following command:

```bash
mvn package
```

This will create a file named `ReportToJira-1.0-SNAPSHOT.jar` in the `target` directory.

## How to Run the Application 🎉

To run the application, use the following command:

```bash
java -jar target/ReportToJira-1.0-SNAPSHOT.jar
```

A window will appear asking for the URL or local file path of your HTML report. Paste the path, click "Create Ticket," and watch the magic happen!

## Screenshots 📸

Here's a sneak peek at the application in action:

![Application Screenshot 1](1_ReportToJira.png)

![Application Screenshot 2](2_ReportToJira.png)

---

And that's it! You're now ready to automate your Jira ticket creation. If you have any questions, feel free to open an issue. Or, you know, just figure it out yourself. We believe in you! 😉