package client.gui.customer;

import client.network.ParkingClient;
import common.enums.MessageType;
import common.model.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerGUI extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(CustomerGUI.class.getName());
    private static final long serialVersionUID = 1L;
    
    private final ParkingClient client;
    private JTextArea outputArea;
    private JButton getTicketButton;
    private JButton payTicketButton;
    private JButton exitButton;
    private JLabel connectionStatus;
    private Timer connectionCheckTimer;
    private JPanel operationsPanel;
    private JPanel statusPanel;

    public CustomerGUI() {
        client = new ParkingClient();
        initializeGUI();
        startConnectionCheck();
    }

    private void initializeGUI() {
        setTitle("Parking Garage - Customer Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create status panel
        createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.NORTH);
        
        // Create center panel for operations
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        // Create operations panel
        createOperationsPanel();
        centerPanel.add(operationsPanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Create output area
        createOutputArea();
        mainPanel.add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // Add window listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });

        setContentPane(mainPanel);
        updateConnectionStatus();
    }

    private void createStatusPanel() {
        statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        
        connectionStatus = new JLabel("●");
        connectionStatus.setForeground(Color.RED);
        
        statusPanel.add(new JLabel("Connection:"));
        statusPanel.add(connectionStatus);
    }
    private JComboBox<String> garageSelector;

    private void createOperationsPanel() {
        operationsPanel = new JPanel();
        operationsPanel.setLayout(new BoxLayout(operationsPanel, BoxLayout.Y_AXIS));
        operationsPanel.setBorder(BorderFactory.createTitledBorder("Operations"));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        
        getTicketButton = new JButton("Get Ticket");
        getTicketButton.addActionListener(e -> getTicket());
        
        payTicketButton = new JButton("Pay Ticket");
        payTicketButton.addActionListener(e -> payTicket());
        
        exitButton = new JButton("Exit Garage");
        exitButton.addActionListener(e -> exitGarage());

        buttonPanel.add(getTicketButton);
        buttonPanel.add(payTicketButton);
        buttonPanel.add(exitButton);

        // Add padding around buttons
        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        paddedPanel.add(buttonPanel, BorderLayout.CENTER);
        
        operationsPanel.add(paddedPanel);
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        selectorPanel.add(new JLabel("Select Garage:"));
        garageSelector = new JComboBox<>(new String[]{"MAIN", "NORTH"});
        selectorPanel.add(garageSelector);

        buttonPanel.add(selectorPanel);
    }

    private void createOutputArea() {
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setMargin(new Insets(5, 5, 5, 5));
        outputArea.setBackground(new Color(245, 245, 245));
    }

    private void startConnectionCheck() {
        connectionCheckTimer = new Timer(2000, e -> updateConnectionStatus());
        connectionCheckTimer.start();
    }

    private void updateConnectionStatus() {
        boolean isConnected = client.isConnected();
        
        SwingUtilities.invokeLater(() -> {
            connectionStatus.setForeground(isConnected ? Color.GREEN : Color.RED);
            
            getTicketButton.setEnabled(isConnected);
            payTicketButton.setEnabled(isConnected);
            exitButton.setEnabled(isConnected);
            
            if (!isConnected) {
                handleDisconnection();
            }
        });
    }

    private void handleDisconnection() {
        outputArea.append("Connection lost. Please wait for reconnection...\n");
    }

    private void checkConnection() throws IOException {
        if (!client.isConnected()) {
            outputArea.append("Connection lost. Attempting to reconnect...\n");
            throw new IOException("Not connected to server");
        }
    }

    private void getTicket() {
        try {
            checkConnection();
            outputArea.append("Requesting new ticket...\n");
            
            String selectedGarage = (String)garageSelector.getSelectedItem();
            Message response = client.sendMessage(new Message(MessageType.CREATE, "TICKET:" + selectedGarage));
            if (response.getType() == MessageType.SUCCESS) {
                String[] parts = response.getData().split(":");
                if (parts.length >= 2) {
                    String ticketBarcode = parts[1];
                    outputArea.append(String.format("Ticket created successfully. Barcode: %s\n", 
                        ticketBarcode));
                    
                    Toolkit.getDefaultToolkit()
                           .getSystemClipboard()
                           .setContents(new StringSelection(ticketBarcode), null);
                           
                    outputArea.append("Ticket barcode copied to clipboard.\n");
                    
                    JOptionPane.showMessageDialog(this,
                        "Ticket created successfully!\nBarcode: " + ticketBarcode + 
                        "\n\nBarcode has been copied to clipboard.",
                        "Ticket Created",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                handleErrorResponse("ticket creation", response.getData());
            }
        } catch (Exception e) {
            handleError("ticket creation", e);
        }
    }
        
    private void payTicket() {
        try {
            checkConnection();
            
            String barcode = JOptionPane.showInputDialog(this, 
                "Enter ticket barcode:", 
                "Pay Ticket",
                JOptionPane.PLAIN_MESSAGE);
                
            if (barcode != null && !barcode.trim().isEmpty()) {
                outputArea.append("Processing payment for ticket: " + barcode + "\n");
                
                Message response = client.sendMessage(
                    new Message(MessageType.CREATE, "PAYMENT:" + barcode + ":CREDIT_CARD")
                );
                
                if (response.getType() == MessageType.SUCCESS) {
                    String[] parts = response.getData().split(":");
                    if (parts.length >= 2) {
                        String transactionId = parts[1];
                        outputArea.append(String.format(
                            "Payment processed successfully. Transaction ID: %s\n", 
                            transactionId));
                            
                        JOptionPane.showMessageDialog(this,
                            "Payment successful!\n" +
                            "Transaction ID: " + transactionId + "\n" +
                            "Please keep your receipt for exit.",
                            "Payment Processed",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    handleErrorResponse("payment", response.getData());
                }
            }
        } catch (Exception e) {
            handleError("payment processing", e);
        }
    }

    private void exitGarage() {
        try {
            checkConnection();
            
            String barcode = JOptionPane.showInputDialog(this,
                "Enter ticket barcode:",
                "Exit Garage",
                JOptionPane.PLAIN_MESSAGE);
                
            if (barcode != null && !barcode.trim().isEmpty()) {
                outputArea.append("Processing exit for ticket: " + barcode + "\n");
                
                Message response = client.sendMessage(
                    new Message(MessageType.CREATE, "EXIT:" + barcode)
                );
                
                if (response.getType() == MessageType.SUCCESS) {
                    outputArea.append("Exit processed successfully. Drive safely!\n");
                    JOptionPane.showMessageDialog(this,
                        "Exit processed successfully!\n" +
                        "Please exit within 15 minutes.\n" +
                        "Thank you for your business!",
                        "Exit Processed",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    handleErrorResponse("exit", response.getData());
                }
            }
        } catch (Exception e) {
            handleError("exit processing", e);
        }
    }

    private void handleError(String operation, Exception e) {
        String errorMessage = String.format("Error during %s: %s\n", operation, e.getMessage());
        outputArea.append(errorMessage);
        LOGGER.log(Level.WARNING, errorMessage, e);
        
        if (e instanceof IOException) {
            updateConnectionStatus();
            if (!client.isConnected()) {
                JOptionPane.showMessageDialog(this,
                    "Lost connection to server. Please try again later.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleErrorResponse(String operation, String errorMessage) {
        outputArea.append(String.format("Failed to process %s: %s\n", operation, errorMessage));
        JOptionPane.showMessageDialog(this,
            errorMessage,
            operation.substring(0, 1).toUpperCase() + operation.substring(1) + " Error",
            JOptionPane.ERROR_MESSAGE);
    }

    private void cleanup() {
        if (connectionCheckTimer != null) {
            connectionCheckTimer.stop();
        }
        if (client != null) {
            client.close();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not set system look and feel", e);
        }
        
        SwingUtilities.invokeLater(() -> {
            CustomerGUI gui = new CustomerGUI();
            gui.setVisible(true);
        });
    }
}