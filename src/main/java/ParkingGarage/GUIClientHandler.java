package ParkingGarage;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;

public class GUIClientHandler {
    private static final Logger LOGGER = Logger.getLogger(GUIClientHandler.class.getName());
    
    private String host;
    private int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Boolean connected;
    private Boolean authenticated;
    private int assignedGarageId;
    private final Timer heartbeatTimer;
    private int reconnectAttempts;
    private static final int MAX_RECONNECT_ATTEMPTS = 3;

    public GUIClientHandler(String host, int port) {
    	this.host = host;
    	this.port = port;
        this.connected = false;
        this.authenticated = false;
        this.heartbeatTimer = new Timer("HeartbeatTimer", true);
        this.reconnectAttempts = 0;
        connect(host, port);
        if (connected) {
            startHeartbeat();
        }
    }

    private void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            socket.setKeepAlive(true);
            socket.setSoTimeout(5000);
            
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            Message connectionMsg = new Message(MessageType.SYSTEM);
            connectionMsg.setData("ping", true);
            out.writeObject(connectionMsg);
            out.flush();
            
            Message response = (Message) in.readObject();
            if (response.getMessageType() == MessageType.SYSTEM && 
                response.getData("pong").equals(true)) {
                connected = true;
                LOGGER.info("Connected to server at " + host + ":" + port);
            } else {
                throw new IOException("Failed to establish connection");
            }
            
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to server", e);
            attemptReconnect(host, port);
        }
    }

    private void attemptReconnect(String host, int port) {
        while (reconnectAttempts < MAX_RECONNECT_ATTEMPTS && !connected) {
            try {
                reconnectAttempts++;
                LOGGER.info("Attempting to reconnect... Attempt " + reconnectAttempts);
                Thread.sleep(1000);
                connect(host, port);
                if (connected) {
                    reconnectAttempts = 0;
                    if (authenticated) {
                        authenticated = false;
                    }
                    return;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Reconnection attempt " + reconnectAttempts + " failed", e);
            }
        }
        throw new RuntimeException("Failed to connect after " + MAX_RECONNECT_ATTEMPTS + " attempts");
    }

    private void startHeartbeat() {
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (connected) {
                    try {
                        Message response = sendMessage(new Message(MessageType.SYSTEM));
                        if (response == null || response.getData("__status__") != MessageStatus.SUCCESS) {
                            handleConnectionFailure();
                        }
                    } catch (IOException e) {
                        handleConnectionFailure();
                    }
                }
            }
        }, 5000, 5000);
    }

    private synchronized void handleConnectionFailure() {
        if (connected) {
            connected = false;
            authenticated = false;
            LOGGER.warning("Connection to server lost");
            try {
                if (socket != null) {
                    socket.close();
                }
                attemptReconnect(this.host, this.port);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error during connection failure handling", e);
            }
        }
    }

    public synchronized Message sendMessage(Message message) throws IOException {
        if (!connected) {
            throw new IOException("Not connected to server");
        }
        
        try {        
            out.writeObject(message);
            out.flush();
            
            Message response = (Message) in.readObject();
            
            if (response.getData("__status__") == MessageStatus.FAILURE) {
                LOGGER.warning("Server error: " + response.getData());
            }
            
            return response;
        } catch (ClassNotFoundException e) {
            throw new IOException("Invalid response from server", e);
        } catch (IOException e) {
            handleConnectionFailure();
            throw e;
        }
    }

    public boolean login(String username, String password) {
        try {
            Message loginMessage = new Message(MessageType.LOGIN);
            loginMessage.setData("username", username);
            loginMessage.setData("password", password);
            Message response = sendMessage(loginMessage);
            
            LOGGER.info("Login response received: " + response.getMessageType() + " - " + response.getData());
            
            if (response.getData("__status__") == MessageStatus.SUCCESS) {
                authenticated = true;
                assignedGarageId = (int) response.getData("defaultGarageId");
                LOGGER.info("Login successful");
                return true;
            }
            
            authenticated = false;
            LOGGER.warning("Login failed: " + response.getData());
            return false;
        } catch (IOException e) {
            authenticated = false;
            LOGGER.log(Level.SEVERE, "Login failed due to connection error", e);
            return false;
        }
    }
    
    public int getAssignedGarageId() {
    	return this.assignedGarageId;
    }
    
    @SuppressWarnings("unchecked")
	public Map<String, Integer> getGarages() {
        try {
	        Message response = sendMessage(new Message(MessageType.GET_GARAGES));
	        return (Map<String, Integer>) response.getData("garages");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to get garages", e);
            return null;
        }
    }

    public synchronized void close() {
        if (!connected) return;
        
        try {
            connected = false;
            authenticated = false;
            heartbeatTimer.cancel();
            
            try {
                sendMessage(new Message(MessageType.QUIT));
            } catch (IOException e) {
                LOGGER.log(Level.FINE, "Error sending disconnect message", e);
            }
            
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();

            LOGGER.info("Disconnected from server");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing connection", e);
        }
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    @SuppressWarnings("removal")
	@Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}