package com.reporttojira;
import javax.swing.*;
import java.awt.event.ActionEvent;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;

/**
 * The main entry point for the application. This class sets up and displays the user interface.
 */
public class Main {

    /**
     * Sets up the main window (JFrame) with a text field for the report URL and a submit button.
     * When the button is clicked, it triggers the report parsing and Jira ticket creation process.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Jira Ticket Creator");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField urlField = new JTextField(30);
        JButton submitButton = new JButton("Create Ticket");

        // This action runs when the "Create Ticket" button is clicked.
        submitButton.addActionListener((ActionEvent e) -> {
            String url = urlField.getText();
            try {
                // 1. Parse the report to get the data.
                ReportData data = ReportParser.parse(url);
                // 2. Create the Jira ticket using the parsed data.
                String ticketId = JiraClient.createTicket(data);
                // 3. Show a success message with the new ticket ID.
                showSuccessDialog(frame, ticketId);
            } catch (Exception ex) {
                // If anything goes wrong, show an error message.
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Set up the layout of the window.
        JPanel panel = new JPanel();
        panel.add(new JLabel("Report URL:"));
        panel.add(urlField);
        panel.add(submitButton);
        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * Displays a success message dialog with the created ticket ID and a "Copy ID" button.
     * @param parent The main window to show the dialog over.
     * @param ticketId The ID of the newly created Jira ticket.
     */
    private static void showSuccessDialog(JFrame parent, String ticketId) {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Ticket Created: " + ticketId));

        JButton copyButton = new JButton("Copy ID");
        copyButton.addActionListener(e -> {
            // This action copies the ticket ID to the system clipboard.
            StringSelection stringSelection = new StringSelection(ticketId);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            copyButton.setText("Copied!");
            copyButton.setEnabled(false); // Disable button after copying.
        });

        panel.add(copyButton);

        JOptionPane.showMessageDialog(parent, panel, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}