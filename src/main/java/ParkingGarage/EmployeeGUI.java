package client.gui.employee;

import client.network.ParkingClient;
import common.enums.MessageType;
import common.model.Message;
import util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeGUI extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(EmployeeGUI.class.getName());
    private static final long serialVersionUID = 1L;
    
    private final ParkingClient client;
    private JTextArea outputArea;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton logoutButton;
    private JButton viewReportButton;
    private JButton manageGarageButton;
    private JPanel operationsPanel;
    private JPanel loginPanel;
    private JLabel connectionStatus;
    private JLabel authenticationStatus;
    private Timer connectionCheckTimer;
    private boolean isLoggedIn;

    public EmployeeGUI() {
        client = new ParkingClient();
        isLoggedIn = false;
        initializeGUI();
        startConnectionCheck();
    }

    private void initializeGUI() {
        setTitle("Parking Garage - Employee Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create status panel
        createStatusPanel();
        mainPanel.add(createStatusPanel(), BorderLayout.NORTH);

        // Create center panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        // Create login and operations panels
        createLoginPanel();
        createOperationsPanel();
        
        centerPanel.add(loginPanel, BorderLayout.NORTH);
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

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        
        connectionStatus = new JLabel("●");
        connectionStatus.setForeground(Color.RED);
        
        authenticationStatus = new JLabel("●");
        authenticationStatus.setForeground(Color.RED);
        
        statusPanel.add(new JLabel("Connection:"));
        statusPanel.add(connectionStatus);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(new JLabel("Authentication:"));
        statusPanel.add(authenticationStatus);
        
        return statusPanel;
    }

    private void createLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Employee Login"));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        inputPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.addActionListener(e -> login());
        inputPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());
        
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        logoutButton.setEnabled(false);

        buttonPanel.add(loginButton);
        buttonPanel.add(logoutButton);

        loginPanel.add(inputPanel);
        loginPanel.add(buttonPanel);
    }

    private void createOperationsPanel() {
        operationsPanel = new JPanel();
        operationsPanel.setLayout(new BoxLayout(operationsPanel, BoxLayout.Y_AXIS));
        operationsPanel.setBorder(BorderFactory.createTitledBorder("Operations"));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        viewReportButton = new JButton("View Reports");
        viewReportButton.setIcon(createImageIcon("/icons/report.png"));
        viewReportButton.addActionListener(e -> viewReport());

        manageGarageButton = new JButton("Manage Garage");
        manageGarageButton.setIcon(createImageIcon("/icons/garage.png"));
        manageGarageButton.addActionListener(e -> manageGarage());

        buttonPanel.add(viewReportButton);
        buttonPanel.add(manageGarageButton);

        // Add padding around buttons
        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        paddedPanel.add(buttonPanel, BorderLayout.CENTER);

        operationsPanel.add(paddedPanel);
        operationsPanel.setVisible(false);
    }

    private void createOutputArea() {
        outputArea = new JTextArea(15, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setMargin(new Insets(5, 5, 5, 5));
        outputArea.setBackground(new Color(245, 245, 245));
    }

    private ImageIcon createImageIcon(String path) {
        try {
            return new ImageIcon(getClass().getResource(path));
        } catch (Exception e) {
            LOGGER.warning("Could not find icon: " + path);
            return null;
        }
    }

    private void startConnectionCheck() {
        connectionCheckTimer = new Timer(2000, e -> updateConnectionStatus());
        connectionCheckTimer.start();
    }

    private void updateConnectionStatus() {
        boolean isConnected = client.isConnected();
        boolean isAuthenticated = client.isAuthenticated();
        
        SwingUtilities.invokeLater(() -> {
            connectionStatus.setForeground(isConnected ? Color.GREEN : Color.RED);
            authenticationStatus.setForeground(isAuthenticated ? Color.GREEN : Color.RED);
            
            loginButton.setEnabled(isConnected && !isAuthenticated);
            logoutButton.setEnabled(isConnected && isAuthenticated);
            
            viewReportButton.setEnabled(isConnected && isAuthenticated);
            manageGarageButton.setEnabled(isConnected && isAuthenticated);
            
            if (!isConnected && isLoggedIn) {
                handleDisconnection();
            }
        });
    }

    private void handleDisconnection() {
        isLoggedIn = false;
        operationsPanel.setVisible(false);
        loginPanel.setVisible(true);
        outputArea.append("Connection lost. Please wait for reconnection...\n");
    }

    private void checkConnection() throws IOException {
        if (!client.isConnected()) {
            outputArea.append("Connection lost. Attempting to reconnect...\n");
            throw new IOException("Not connected to server");
        }
        if (!client.isAuthenticated() && isLoggedIn) {
            outputArea.append("Session expired. Please log in again.\n");
            handleLogout();
            throw new IOException("Session expired");
        }
    }

    private void login() {
        try {
            checkConnection();
            
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter both username and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            outputArea.append("Attempting login...\n");
            
            if (client.login(username, password)) {
                isLoggedIn = true;
                outputArea.append("Login successful!\n");
                
                loginPanel.setVisible(false);
                operationsPanel.setVisible(true);
                
                // Clear sensitive data
                passwordField.setText("");
                usernameField.setText("");
                
                updateConnectionStatus();
            } else {
                outputArea.append("Login failed. Please check your credentials.\n");
                passwordField.setText("");
                JOptionPane.showMessageDialog(this,
                    "Invalid credentials",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            handleError("login", e);
            passwordField.setText("");
            handleLogout();
        }
    }

    private void logout() {
        try {
            if (isLoggedIn) {
                outputArea.append("Logging out...\n");
                handleLogout();
                outputArea.append("Logout successful.\n");
            }
        } catch (Exception e) {
            handleError("logout", e);
        }
    }

    private void handleLogout() {
        isLoggedIn = false;
        loginPanel.setVisible(true);
        operationsPanel.setVisible(false);
        updateConnectionStatus();
    }

    private void viewReport() {
        try {
            checkConnection();
            
            // Format dates properly
            LocalDateTime start = LocalDateTime.now().minusDays(30);
            LocalDateTime end = LocalDateTime.now();
            String dateRange = String.format("%s,%s",  // Use comma instead of colon
                start.format(Constants.DATE_TIME_FORMATTER),
                end.format(Constants.DATE_TIME_FORMATTER));
                        
            Message response = client.sendMessage(
                new Message(MessageType.REPORT, "SPACE:" + dateRange)
            );
            
            if (response.getType() == MessageType.SUCCESS) {
                outputArea.append("\nReport Generated:\n");
                outputArea.append("----------------------------------------\n");
                outputArea.append(response.getData());
                outputArea.append("----------------------------------------\n");
                
                // Save report option
                int option = JOptionPane.showConfirmDialog(this,
                    "Would you like to save this report?",
                    "Save Report",
                    JOptionPane.YES_NO_OPTION);
                    
                if (option == JOptionPane.YES_OPTION) {
                    saveReport(response.getData());
                }
            } else {
                handleErrorResponse("report generation", response.getData());
            }
        } catch (Exception e) {
            handleError("report generation", e);
        }
    }

    private void saveReport(String reportData) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        fileChooser.setSelectedFile(new java.io.File("report.txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                java.nio.file.Files.writeString(file.toPath(), reportData);
                JOptionPane.showMessageDialog(this,
                    "Report saved successfully!",
                    "Save Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                handleError("saving report", e);
            }
        }
    }

    private void manageGarage() {
        try {
            checkConnection();
            outputArea.append("Retrieving garage status...\n");
            
            Message response = client.sendMessage(
                new Message(MessageType.TARGET, "GARAGE:MAIN")
            );
            
            if (response.getType() == MessageType.SUCCESS) {
                String[] garageInfo = response.getData().split("\n");
                outputArea.append("\nGarage Status:\n");
                outputArea.append("----------------------------------------\n");
                for (String info : garageInfo) {
                    outputArea.append(info + "\n");
                }
                outputArea.append("----------------------------------------\n");
                
                showGarageManagementDialog(garageInfo);
            } else {
                handleErrorResponse("garage status retrieval", response.getData());
            }
        } catch (Exception e) {
            handleError("garage management", e);
        }
    }

    private void showGarageManagementDialog(String[] garageInfo) {
        JDialog dialog = new JDialog(this, "Garage Management", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        // Create tabbed pane for different management options
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Maintenance tab
        tabbedPane.addTab("Maintenance", createMaintenancePanel());
        
        // Capacity tab
        tabbedPane.addTab("Capacity", createCapacityPanel());
        
        // Rates tab
        tabbedPane.addTab("Rates", createRatesPanel());
        
        dialog.add(tabbedPane, BorderLayout.CENTER);
        
        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private JPanel createMaintenancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea reasonArea = new JTextArea(5, 30);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton enableButton = new JButton("Enable Maintenance");
        JButton disableButton = new JButton("Disable Maintenance");
        
        enableButton.addActionListener(e -> {
            String reason = reasonArea.getText().trim();
            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please provide a maintenance reason",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            handleMaintenanceMode(true, reason);});
        
            disableButton.addActionListener(e -> handleMaintenanceMode(false, null));
            
            buttonPanel.add(enableButton);
            buttonPanel.add(disableButton);
            
            panel.add(new JLabel("Maintenance Reason:"), BorderLayout.NORTH);
            panel.add(new JScrollPane(reasonArea), BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            return panel;
        }
    
        private JPanel createCapacityPanel() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(
                Constants.DEFAULT_GARAGE_CAPACITY,
                Constants.MIN_GARAGE_CAPACITY,
                Constants.MAX_GARAGE_CAPACITY,
                10
            ));
            
            inputPanel.add(new JLabel("New Capacity:"));
            inputPanel.add(capacitySpinner);
            
            JButton updateButton = new JButton("Update Capacity");
            updateButton.addActionListener(e -> 
                handleCapacityAdjustment((Integer)capacitySpinner.getValue()));
            
            panel.add(inputPanel, BorderLayout.CENTER);
            panel.add(updateButton, BorderLayout.SOUTH);
            
            return panel;
        }
    
        private JPanel createRatesPanel() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JPanel inputPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Hourly rate
            SpinnerNumberModel hourlyModel = new SpinnerNumberModel(
                Constants.DEFAULT_FEE_HOURLY,
                Constants.MIN_RATE,
                Constants.MAX_RATE,
                50
            );
            JSpinner hourlySpinner = new JSpinner(hourlyModel);
            
            gbc.gridx = 0; gbc.gridy = 0;
            inputPanel.add(new JLabel("Hourly Rate (cents):"), gbc);
            gbc.gridx = 1;
            inputPanel.add(hourlySpinner, gbc);
            
            JButton updateButton = new JButton("Update Rates");
            updateButton.addActionListener(e -> 
                handleRateUpdate((Integer)hourlySpinner.getValue()));
            
            panel.add(inputPanel, BorderLayout.CENTER);
            panel.add(updateButton, BorderLayout.SOUTH);
            
            return panel;
        }
    
        private void handleMaintenanceMode(boolean enable, String reason) {
            try {
                checkConnection();
                
                Message response = client.sendMessage(
                    new Message(MessageType.UPDATE,
                        enable ? "MAINTENANCE:ON:" + reason : "MAINTENANCE:OFF")
                );
                
                if (response.getType() == MessageType.SUCCESS) {
                    String msg = enable ? "Maintenance mode enabled" : "Maintenance mode disabled";
                    outputArea.append(msg + "\n");
                    JOptionPane.showMessageDialog(this,
                        msg,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    handleErrorResponse("maintenance mode update", response.getData());
                }
            } catch (Exception e) {
                handleError("maintenance mode update", e);
            }
        }
    
        private void handleCapacityAdjustment(int newCapacity) {
            try {
                checkConnection();
                
                Message response = client.sendMessage(
                    new Message(MessageType.UPDATE, "CAPACITY:" + newCapacity)
                );
                
                if (response.getType() == MessageType.SUCCESS) {
                    outputArea.append("Capacity updated successfully\n");
                    JOptionPane.showMessageDialog(this,
                        "Capacity updated to " + newCapacity,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    handleErrorResponse("capacity update", response.getData());
                }
            } catch (Exception e) {
                handleError("capacity adjustment", e);
            }
        }
    
        private void handleRateUpdate(int newRate) {
            try {
                checkConnection();
                
                Message response = client.sendMessage(
                    new Message(MessageType.UPDATE, "RATE:HOURLY:" + newRate)
                );
                
                if (response.getType() == MessageType.SUCCESS) {
                    outputArea.append("Rate updated successfully\n");
                    JOptionPane.showMessageDialog(this,
                        String.format("Rate updated to $%.2f/hour", newRate/100.0),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    handleErrorResponse("rate update", response.getData());
                }
            } catch (Exception e) {
                handleError("rate update", e);
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
                EmployeeGUI gui = new EmployeeGUI();
                gui.setVisible(true);
            });
        }
    }