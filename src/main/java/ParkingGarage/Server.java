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
    private ClientHandlerFacade facade;
    
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // initialize streams
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            facade = new ClientHandlerFacade(socket, in, out);

            Message message;

            // while loop to receive messages from client
            while ((message = (Message) in.readObject()) != null) {
                System.out.println("Received [" + message.getMessageType() + "] message");

                MessageType messageType = message.getMessageType();

                // handle messages types
                switch (messageType) {
	                case CHECK_TICKET:
	                	facade.handleCheckTicket(message);
	                	break;
	                case CREATE_USER:
	                	facade.handleCreateUser(message);
	                    break;
                    case GET_GARAGES:
                    	facade.handleGetGarages(message);
                    	break;
                    case ENTER_GARAGE:
                    	facade.handleEnterGarage(message);
                        break;
                    case EXIT_GARAGE:
                    	facade.handleExitGarage(message);
                        break;
                    case LOGIN:
                    	facade.handleLogin(message);
                        break;
                    case LOGOUT:
                    	facade.handleLogout(message);
                        break;
                    case PAYMENT:
                    	facade.handlePayment(message);
                    	break;
                    case REPORT:
                    	facade.handleReport(message);
                        break;
	                case SYSTEM:
	                	facade.handleSystem(message);
	                	break;
                    case QUIT:
                    	facade.handleQuit(message);
                        return;
                    case UPDATE_FEE:
                    	facade.handleUpdateFee(message);
                    	break;
                    case UPDATE_GARAGE_CAPACITY:
                    	facade.handleUpdateGarageCapacity(message);
                    	break;
                    default:
                        System.out.println("Unknown message type received.");
                        message.setData("__status__", MessageStatus.FAILURE);
                        message.setData("message", "Unknown message type.");
                        out.writeObject(message);
                        out.flush();
                }
            }
        
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}


