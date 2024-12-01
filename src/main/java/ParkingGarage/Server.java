package ParkingGarage;

import java.io.IOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    // port number
    private static final int port = 12345;

    public static void main(String[] args) {
        System.out.println("Server is running on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                // accept client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                // create new client handler for each connected client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();

            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket; // socket to communicate with client
    private ObjectOutputStream out; // output stream to send messages to client
    private ObjectInputStream in; // input stream to receive messages from client
    
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // initialize streams
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            Message message;

            // while loop to receive messages from client
            while ((message = (Message) in.readObject()) != null) {
                System.out.println("Received [" + message.getMessageType() + "] message");

                MessageType messageType = message.getMessageType();

                // handle messages types
                switch (messageType) {
                    case LOGIN:
                        handleLogin(message);
                        break;
                    case CREATE_USER:
                        handleCreateUser(message);
                        break;
                    case ENTER_GARAGE:
                        handleEnterGarage(message);
                        break;
                    case EXIT_GARAGE:
                        handleExitGarage(message);
                        break;
                    case REPORT:
                        handleReport(message);
                        break;
                    case LOGOUT:
                        handleLogout(message);
                        break;
                    case QUIT:
                        handleQuit(message);
                        return;
                    default:
                        System.out.println("Unknown message type received.");
                        sendErrorResponse("Unknown message type.");
                }
            }
        
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}


