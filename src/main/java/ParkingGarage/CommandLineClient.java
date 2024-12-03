package ParkingGarage;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandLineClient {
	private String host;
	private int port;
	
	public CommandLineClient(String host, int port) {
		this.host = host;
		this.port = port;
		run();
	}

    public void run() {

        try {
            // Create Scanner and prompt for server details
            Scanner sc = new Scanner(System.in);
            // System.out.print("Enter the port number (default 12345): ");
            // int port = sc.nextInt();
            // sc.nextLine(); // Consume newline

            // System.out.print("Enter server address: ");
            // String host = sc.nextLine();

            // Connect to server
            Socket socket = new Socket(host, port);
            System.out.println("Connected to " + host + ":" + port);

            // Create object streams
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            boolean running = true;
            boolean authenticated = false;
            String userRole = "";
            int userId = -1; // To store the logged-in user's ID

            while (running) {
                if (!authenticated) {
                    // Prompt the user for authentication or registration
                    System.out.print("Do you have an account? (yes/no): ");
                    String hasAccount = sc.nextLine().trim().toLowerCase();

                    if (hasAccount.equals("yes")) {
                        // Existing user login
                        System.out.print("Enter your username: ");
                        String username = sc.nextLine();

                        System.out.print("Enter your password: ");
                        String password = sc.nextLine();

                        // Create LOGIN message
                        Map<String, Object> loginData = new HashMap<>();
                        loginData.put("username", username);
                        loginData.put("password", password);

                        Message loginMessage = new Message(MessageType.LOGIN, loginData);
                        out.writeObject(loginMessage);

                        // Receive and handle response
                        Message loginResponse = (Message) in.readObject();
                        boolean success = (Boolean) loginResponse.getData("success");
                        if (success) {
                            authenticated = true;
                            userRole = (String) loginResponse.getData("role");
                            userId = (Integer) loginResponse.getData("userId");
                            System.out.println("Login successful! Welcome, " + username + ".");
                        } else {
                            String error = (String) loginResponse.getData("error");
                            System.out.println("Login failed: " + error);
                        }
                    } else if (hasAccount.equals("no")) {
                        // New user registration
                        System.out.print("Enter your name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter your desired username: ");
                        String username = sc.nextLine();

                        System.out.print("Enter your password: ");
                        String password = sc.nextLine();

                        System.out.print("Enter your role (EMPLOYEE or ADMIN): ");
                        String roleInput = sc.nextLine().toUpperCase();
                        RoleType role;
                        try {
                            role = RoleType.valueOf(roleInput);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid role. Defaulting to EMOPLOYEE.");
                            role = RoleType.EMPLOYEE;
                        }

                        System.out.print("Enter your assigned garage ID: ");
                        int assignedGarageId = sc.nextInt();
                        sc.nextLine(); // Consume newline

                        // Create data map
                        Map<String, Object> newData = new HashMap<>();
                        newData.put("name", name);
                        newData.put("username", username);
                        newData.put("password", password);
                        newData.put("role", role.toString());
                        newData.put("assignedGarageId", assignedGarageId);

                        // Create and send CREATE_USER message
                        Message createUserMessage = new Message(MessageType.CREATE_USER, newData);
                        out.writeObject(createUserMessage);

                        // Receive and handle response
                        Message newResponse = (Message) in.readObject();
                        boolean success = (Boolean) newResponse.getData("success");
                        if (success) {
                            userId = (Integer) newResponse.getData("userId");
                            userRole = role.toString();
                            authenticated = true;
                            System.out.println("User created successfully! Your user ID is: " + userId);
                        } else {
                            String error = (String) newResponse.getData("error");
                            System.out.println("Error creating user: " + error);
                        }
                    } else {
                        System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                    }
                    System.out.println("----------------------------");
                    continue; // Restart the loop after authentication attempt
                }

                // Prompt for action
                System.out.print("Enter action (ENTER, EXIT, REPORT, LOGOUT, QUIT): ");
                String actionInput = sc.nextLine().trim().toUpperCase();

                MessageType messageType;
                Message message = null;

                switch (actionInput) {
                    case "ENTER":
                        messageType = MessageType.ENTER_GARAGE;

                        // Collect necessary data (userId is already known)
                        Map<String, Object> enterData = new HashMap<>();
                        enterData.put("userId", userId);

                        // Create and send message
                        message = new Message(messageType, enterData);
                        out.writeObject(message);

                        // Receive and handle response
                        Message enterResponse = (Message) in.readObject();
                        System.out.println("Server response: " + enterResponse);

                        break;

                    case "EXIT":
                        messageType = MessageType.EXIT_GARAGE;

                        // Collect necessary data
                        System.out.print("Enter your ticket ID: ");
                        int ticketId = sc.nextInt();
                        sc.nextLine(); // Consume newline

                        Map<String, Object> exitData = new HashMap<>();
                        exitData.put("ticketId", ticketId);

                        // Create and send message
                        message = new Message(messageType, exitData);
                        out.writeObject(message);

                        // Receive and handle response
                        Message exitResponse = (Message) in.readObject();
                        System.out.println("Server response: " + exitResponse);

                        break;

                    case "REPORT":
                        messageType = MessageType.REPORT;

                        // Client-side role check
                        if (!userRole.equals("EMPLOYEE")) {
                            System.out.println("Access denied: Only employees can access reports.");
                            break;
                        }

                        // Prompt the user for the report type
                        System.out.print("Enter report type (DAILY, WEEKLY, MONTHLY): ");
                        String reportTypeInput = sc.nextLine().trim().toUpperCase();

                        // Validate the input
                        if (!reportTypeInput.equals("DAILY") && !reportTypeInput.equals("WEEKLY") && !reportTypeInput.equals("MONTHLY")) {
                            System.out.println("Invalid report type. Please enter DAILY, WEEKLY, or MONTHLY.");
                            break;
                        }

                        // Create data map with the report type
                        Map<String, Object> reportData = new HashMap<>();
                        reportData.put("reportType", reportTypeInput);

                        // Create and send the message with the report type
                        message = new Message(messageType, reportData);
                        out.writeObject(message);

                        // Receive and handle the server's response
                        Message reportResponse = (Message) in.readObject();
                        System.out.println("Server response: " + reportResponse);

                        break;

                    case "LOGOUT":
                        // Log out the user
                        authenticated = false;
                        userRole = "";
                        userId = -1;
                        System.out.println("You have been logged out.");
                        break;

                    case "QUIT":
                        messageType = MessageType.QUIT;

                        // Create and send quit message
                        message = new Message(messageType);
                        out.writeObject(message);

                        System.out.println("Exiting...");
                        running = false; // Exit the loop
                        break;

                    default:
                        System.out.println("Invalid action.");
                        break;
                }

                System.out.println("----------------------------");
            }

            // Close resources
            in.close();
            out.close();
            socket.close();
            sc.close();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}