package com.beacon.client;
import com.beacon.gui.GUI; // Import the GUI class

import java.awt.event.*; // AWT event imports for the action listener
import java.net.Socket; // Socket import for connecting to the server
import java.net.UnknownHostException; // UnknownHostException import for handling unknown host errors
import java.nio.charset.StandardCharsets; // Charset import for UTF-8 encoding
import java.io.*; // IO imports for reading and writing
import javax.swing.*; // Swing imports

/**
 * Client class that connects to the server and sends messages to the server.
 * @see ActionListener for more information on ActionListener.
 * @author Oliver, Matias
 */
public class Client {
    // Connection variables
    private String hostname; // Hostname of the server
    private int port; // Port to connect on

    private String username; // Username, message being sent to the server

    // I/O variables
    private PrintWriter writer; // Used for writing messages to the server
    private BufferedReader reader; // Used for reading messages to the server
    public Encryptor encryptor; // Used for encrypting and decrypting messages

    public boolean isRunning = true; // If true, the client is running

    /**
     * Constructor for the Client class.
     * Creates all the components for the window.
     * @param hostname the hostname of the server
     * @param port the port to connect on
     * @author Oliver
     */
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        isRunning = true;
    }

    /**
     * Prompts the user to input a password through a dialog box.
     * @return null if the user presses cancel, the password string if the user presses ok
     * @author Oliver
     */
    public String getPassword() {
        JPasswordField passworldField = new JPasswordField(20);
        Object[] message = {
                "Password:", passworldField
        };
        int result = JOptionPane.showConfirmDialog(null, message, "Beacon", JOptionPane.OK_CANCEL_OPTION);
        // If the presses cancel, return null
        if (result == JOptionPane.OK_OPTION) return new String(passworldField.getPassword());
        else return null;
    }

    /**
     * Prompts the user to input a hostname and port through a dialog box. Used in the main method, before the client is initialized.
     * @return An array of strings containing the hostname and port.
     * @author Oliver, Matias
     */
    public static String[] getConnection() {
        JPanel panel = new JPanel();
        panel.setLayout(new SpringLayout()); // Gives the dialogue box a nice layout

        JTextField hostnameField = new JTextField(20);
        JTextField portField = new JTextField(20);

        Object[] message = {
                "Host name:", hostnameField,
                "Port:", portField
        };

        // Keep running until the user enters a valid hostname and port
        int result = 0;
        do {
            result = JOptionPane.showConfirmDialog(null, message, "Beacon", JOptionPane.OK_CANCEL_OPTION);
            // A lot of error handling for the connection dialog
            if (result == JOptionPane.OK_OPTION) {
                if (hostnameField.getText().equals("") && portField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter a host name and port number.", "Beacon", JOptionPane.ERROR_MESSAGE);
                } else if (portField.getText().matches("[a-zA-Z]+")) {
                    JOptionPane.showMessageDialog(null, "Please enter a number for the port.", "Beacon", JOptionPane.ERROR_MESSAGE);
                } else if (portField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter a port number.", "Beacon", JOptionPane.ERROR_MESSAGE);
                } else if (hostnameField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter a host name.", "Beacon", JOptionPane.ERROR_MESSAGE);
                } else { // The good case
                    System.out.println("hostname: " + hostnameField.getText() + ", port: " + portField.getText());
                    return new String[] {hostnameField.getText(), portField.getText()};
                }
            } else {
                return null;
            }
        } while (result == JOptionPane.OK_OPTION);
        return null; // This should never be reached, but it is here to make the compiler happy
    }

    /**
     * Getter for the username.
     * @return the username of the client as a string
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for the username.
     * @param username the username of the client as a string
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Runs the client-side code.
     * Contains the socket connection to the server, writing messages to the server, and GUI.
     * @throws UnknownHostException if the hostname is not found
     * @throws IOException if there is an I/O server
     * @throws Exception if there is a strange error
     * @return true if the client runs successfully, false if there is an error
     * @author Oliver
     */
    public boolean run() {
        try {
            // Start a socket at hostname and port
            Socket socket = new Socket(hostname, port); 
            // Creates the GUI for the client (the message box and the send button
            GUI gui = new GUI(socket, this); 
            // Initializing readers and writers
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            // Gets and checks the password provided by the user
            String response = "";
            String password = "";
            while (!response.equals("correctpassword")) { // While the password is incorrect, keep asking for a password
                password = getPassword(); // Retrieve password
                if (password == null) { // If the user presses cancel, exit the program
                    socket.close();
                    System.exit(0);
                }
                Thread.sleep(100); // Delay for the server to catch up (this is a hacky solution for bad wifi, but it works)
                writer.println(password); // Send the password to the server
                response = reader.readLine(); // Get the response from the server
                Thread.sleep(100);

                if (response.equals("incorrectpassword")) { // If the password is incorrect, display an error message
                    JOptionPane.showMessageDialog(null, "Incorrect password.", "Beacon", JOptionPane.ERROR_MESSAGE);
                }
            }
            
            encryptor = new Encryptor(password); // Create an encryptor with the password

            gui.createFrame(gui); // Creates the frame for the GUI

            System.out.println("Connected to the chat server"); // Debugging message
            gui.addMessage("Connected to the chat server on address " + hostname + " on port " + port + ".", gui.serverstyle);

            // Get the username from the user 
            username = JOptionPane.showInputDialog("Enter a username: ");
            if (username == null) { // Exit program if the user presses cancel
                socket.close();
                System.exit(0);
            } else if (username.equals("")) { // If the user enters an empty string, set the username to "Anonymous"
                username = "Anonymous";
            } else if (username.length() > 20) { // If the username is longer than 20 characters, truncate it
                username = username.substring(0, 20);
            }

            gui.addMessage("Welcome to the chat, " + username + "!", gui.serverstyle);

            gui.sendMessage(encryptor.encrypt(username)); // Sends the username to the server

            // Start the ReadThread for asynchronously reading messages from the server
            ReadThread readThread = new ReadThread(socket, this, gui);
            readThread.start();

            return true;
        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Server not found: " + e.getMessage(), "Beacon", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "I/O Error: " + e.getMessage(), "Beacon", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Beacon", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Main method for the client, handles for the command line arguments and initializing the client object
     * @param args command line arguments, should never be used
     * @author Oliver
     */
    public static void main(String[] args) {
        // Store the string connection information 
        String[] connectionInfo = getConnection();

        // if the connection info is null (the user pressed cancel), exit the program
        if (connectionInfo == null) System.exit(0);
        
        // Initiate the client object that is passed to the user thread 
        Client client = new Client(connectionInfo[0], Integer.parseInt(connectionInfo[1]));

        // keep running the connection box until the user enters a valid hostname and port
        while(!client.run()) {
            connectionInfo = getConnection();
            if (connectionInfo == null) System.exit(0);
            client = new Client(connectionInfo[0], Integer.parseInt(connectionInfo[1]));
        }
    }
}
