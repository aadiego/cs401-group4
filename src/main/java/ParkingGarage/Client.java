package ParkingGarage;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    
    public static void main(String[] args) {

        try {
            // Create inputStream, assign user given port number
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter the port number: <12345> ");
            int port = sc.nextInt();

            // Take server address from user 
            System.out.print("Enter server address: ");
            String host = sc.next();

            // connect to server
            Socket socket = new Socket(host, port);
            System.out.println("Connected to " + host + ":" + port);

            // create object outputStream and inputStream
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                // prompt for message type
                System.out.print("Enter message type (Login, enter, exit, report, quit)");
                String typeInput = sc.nextLine().trim().toUpperCase();

                // not sure what message attributes are valuable
                MessageType messageType;
                String status;
                String content;
                
                // determine message type
                switch (typeInput) {
                    case "LOGIN": 
                        messageType = MessageType.LOGIN;
                        status = "Pending";
                        content = "Login attempt";
                        break;
                    case "ENTER":
                        messageType = MessageType.ENTER;
                        status = "Pending";
                        content = "Trying to enter garage";
                        break;
                    case "EXIT":
                        messageType = MessageType.EXIT;
                        status = "Pending";
                        content = "Trying to exit garage";
                        break;
                    case "REPORT":
                        messageType = MessageType.REPORT;
                        System.out.println("Which report would you like? (Daily, Weekly, Monthly)");
                        status = sc.nextLine().trim().toUpperCase();
                        content = "Requesting report";
                        break;
                    case "QUIT":
                        messageType = MessageType.QUIT;
                        status = "Pending";
                        content = "Exiting program";
                        break;
                    default:
                        System.out.println("Invalid message type.");
                        continue;
                }

                // exit loop if user wants to quit
                if (messageType == MessageType.QUIT) {
                    System.out.println("Exiting...");
                    // Send exit message 
                    Message exitMessage = new Message(messageType, status, content);
                    out.writeObject(exitMessage);
                    break;
                }

                // create and send message to server 
                Message message = new Message(messageType, status, content);
                out.writeObject(message);

                // receive response from server 
                Message response = (Message) in.readObject();
                System.out.println("Server: " + response.getContent());

            }

            // close resources
            in.close();
            out.close();
            socket.close();
            sc.close();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        } 
    }
}
