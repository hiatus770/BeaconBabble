import java.awt.event.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

/**
 * Client class that connects to the server and sends messages to the server.
 * Inherits the JPanel class for the GUI and implements ActionListener for polling keyboard input.
 * @see javax.swing.JPanel for more information on JPanel.
 * @see ActionListener for more information on ActionListener.
 * @author goose, hiatus
 */
public class Client {
    private String hostname; // hostname of the server
    private int port; // port to connect on
    private String username; // username, message being sent to the server
    private PrintWriter writer; // used for writing messages to the server
    private BufferedReader reader;
    private boolean fullDebug = true; // if true, prints out all the debug messages

    // GUI components are public to avoid needing getters and setters for them as they are accessed in the read thread
    public JTextArea incomingMessageBox;
    public JTextField outgoingMessage;
    public JMenuBar menuBar;
    public JFrame frame;

    public Icon icon = new ImageIcon(getClass().getResource("resources/icon.png"));

    /**
     * Constructor for the Client class.
     * Creates all the components for the window.
     * @param hostname the hostname of the server
     * @param port the port to connect on
     * @author goose
     */
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getPassword() {
        JPasswordField passworldField = new JPasswordField(20);
        Object[] message = {
                "Password:", passworldField
        };
        int result = JOptionPane.showConfirmDialog(null, message, "Beacon", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            return new String(passworldField.getPassword());
        } else {
            return null;
        }
    }

    /**
     * Prompts the user to input a hostname and port through a dialog box.
     * @return An array of strings containing the hostname and port.
     * @author goose, hiatus
     */
    public static String[] getConnection() {
        JPanel panel = new JPanel();
        panel.setLayout(new SpringLayout());

        JTextField hostnameField = new JTextField(20);
        JTextField portField = new JTextField(20);

        Object[] message = {
                "Host name:", hostnameField,
                "Port:", portField
        };

        // keep running until the user enters a valid hostname and port
        do {
            int result = JOptionPane.showConfirmDialog(null, message, "Beacon", JOptionPane.OK_CANCEL_OPTION);
            // a lot of error handling for the connection dialog
            if (result == JOptionPane.OK_OPTION) {
                if (hostnameField.getText().equals("") && portField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter a host name and port number.", "Beacon", JOptionPane.ERROR_MESSAGE);
                } else if (portField.getText().matches("[a-zA-Z]+")) {
                    JOptionPane.showMessageDialog(null, "Please enter a number for the port.", "Beacon", JOptionPane.ERROR_MESSAGE);
                } else if (portField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter a port number.", "Beacon", JOptionPane.ERROR_MESSAGE);
                } else if (hostnameField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter a host name.", "Beacon", JOptionPane.ERROR_MESSAGE);
                } else {
                    System.out.println("hostname: " + hostnameField.getText() + ", port: " + portField.getText());
                    return new String[] {hostnameField.getText(), portField.getText()};
                }
            } else {
                return null;
            }
        } while (true);
    }

    public String getUsername() {
        return username;
    }

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
     * @author goose
     */
    public boolean run() {
        try {
            // Start a socket at hostname and port
            Socket socket = new Socket(hostname, port); 
            // Creates the GUI for the client (the message box and the send button
            GUI gui = new GUI(socket, this); 
            // Initializing readers and writers
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Gets and checks the password provided by the user
            String response = "";
            while (!response.equals("correctpassword")) { // While the password is incorrect, keep asking for a password
                String password = getPassword(); // Retrieve password
                if (password == null) { // If the user presses cancel, exit the program
                    writer.println("/exit");
                    socket.close();
                    System.exit(0);
                }
                writer.println(password); // Send the password to the server
                response = reader.readLine(); // Get the response from the server
                if (response.equals("incorrectpassword")) { // If the password is incorrect, display an error message
                    JOptionPane.showMessageDialog(null, "Incorrect password.", "Beacon", JOptionPane.ERROR_MESSAGE);
                }
            }

            gui.createFrame(gui); // Creates the frame for the GUI
            System.out.println("Connected to the chat server");
            gui.incomingMessages.insertString(gui.incomingMessages.getLength(), "Connected to the chat server on address " + hostname + " on port " + port + ".\n", gui.serverstyle);

            // Get the username from the user 
            username = JOptionPane.showInputDialog("Enter a username: ");
            if (username == null) {
                writer.println("/exit");
                socket.close();
                System.exit(0);
            } else if (username.equals("")) {
                username = "Anonymous";
            }

            gui.incomingMessages.insertString(gui.incomingMessages.getLength(), "Welcome to the chat, " + username + "!\n", gui.serverstyle);
            
            writer.println(username); // sending the username to the server

            // Start the read thread for the program, this will add any received messages to the incomingMessageBox
            ReadThread readThread = new ReadThread(socket, this, gui);
            readThread.start(); // Start the ReadThread

            return true;
        } catch (UnknownHostException e) {
            if (fullDebug) System.out.println("Server not found: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Server not found: " + e.getMessage(), "Beacon", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException e) {
            if (fullDebug) System.out.println("I/O Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "I/O Error: " + e.getMessage(), "Beacon", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            if (fullDebug) System.out.println("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Beacon", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Main method for the client, handles for the command line arguments and initializing the client object
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
