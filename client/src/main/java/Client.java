import java.awt.*;
import java.awt.event.*;
import java.awt.TrayIcon.MessageType;
import java.net.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

/**
 * Client class that connects to the server and sends messages to the server.
 * Inherits the JPanel class for the GUI and implements ActionListener for polling keyboard input.
 * @see javax.swing.JPanel for more information on JPanel.
 * @see ActionListener for more information on ActionListener.
 * @author goose and hiatus
 */
public class Client extends JPanel implements ActionListener {
    private String hostname; // hostname of the server
    private int port; // port to connect on
    private String username, message; // username, message being sent to the server
    private PrintWriter writer; // used for writing messages to the server
    private String timeStamp; // the time stamp of the message
    private boolean fullDebug = true; // if true, prints out all the debug messages

    // GUI components are public to avoid needing getters and setters for them as they are accessed in the read thread
    public JTextArea incomingMessageBox;
    public JTextField outgoingMessage;
    public JFrame frame;

    /**
     * Constructor for the Client class.
     * Creates all the components for the window.
     * @param hostname the hostname of the server
     * @param port the port to connect on
     * @author goose
     */
    public Client(String hostname, int port) {
        super(new GridBagLayout());

        message = ""; // sets the message being sent to an empty string

        this.hostname = hostname;
        this.port = port;

        // make a text area for incoming messages
        incomingMessageBox = new JTextArea();
        incomingMessageBox.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(incomingMessageBox); // scrollable pane for the message box

        // make a text input box for outgoing messages
        outgoingMessage = new JTextField();
        outgoingMessage.addActionListener(this);

        // make a send button
        JButton sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(70, outgoingMessage.getPreferredSize().height)); // setting a small preferred size for the button
        sendButton.addActionListener(this);

        // constraint properties for the outgoing message box
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        constraints.gridy = 1; // sets the y position of the component to the second grid spot
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 2.0;
        add(outgoingMessage, constraints);

        // constraint properties for the button
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.LINE_END;
        // setting weightx to 0 makes sure that the button stays in its own grid spot and doesnt take up like half the space of the message box
        // if you're wondering what that means you can change it to 1 and see for yourself
        constraints.weightx = 0;
        add(sendButton, constraints); // adds the button right next to the message box

        // constraint properties for the incoming message box
        constraints.gridy = 0; // sets the x position of the component to the first grid spot
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(scrollPane, constraints);
    }

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

    /**
     * Creates a new frame for the client.
     * @param client the client that is creating the frame
     */
    private void createFrame(Client client) {
        frame = new JFrame("Beacon");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // If the user closes the window then send the exit window 
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                writer.println("/exit");
                System.out.println("Exiting!");
            }
        });

        frame.setResizable(true); // allow frame to be resized
        frame.setSize(400, 300); // set dimensions
        frame.add(client);
        frame.setVisible(true);
        Image icon = new ImageIcon(getClass().getResource("resources/icon.png")).getImage();
        try {
            frame.setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Could not find icon.png");
        }
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
        System.out.println("INSIDE RUN(); Connecting to " + hostname + " on port " + port); 
        try {
            // Start a socket at the hostname and port
            Socket socket = new Socket(hostname, port); // creates a socket and connects it to the specified port number at the specified IP address

            // Writing to the server
            writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to the chat server");
            incomingMessageBox.setText("Connected to the chat server on address " + hostname + " on port " + port + ".");
            createFrame(this); // creates the window frame for the client
            
            // Get the username from the user 
            username = JOptionPane.showInputDialog("Enter a username: ");
            incomingMessageBox.setText(incomingMessageBox.getText() + "\nWelcome to the chat, " + username + "!");
            writer.println(username); // sending the username to the server

            // Start the read thread for the program, this will add any received messages to the incomingMessageBox
            ReadThread readThread = new ReadThread(socket, this);
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
     * @param args bruh
     */
    public static void main(String[] args) {
        // store connection info into a string array
        String[] connectionInfo = getConnection();

        // if the connection info is null (the user pressed cancel), exit the program
        if (connectionInfo == null) System.exit(0);
        System.out.println("hostname: " + connectionInfo[0] + ", port: " + connectionInfo[1]);
        Client client = new Client(connectionInfo[0], Integer.parseInt(connectionInfo[1]));

        // keep running the client until the user enters a valid hostname and port
        // while(!client.run()) {
        //     connectionInfo = getConnection();
        //     if (connectionInfo == null) System.exit(0);
        //     client = new Client(connectionInfo[0], Integer.parseInt(connectionInfo[1]));
        // }
        client.run();
    }

    /**
     * Processes the event when the user presses the enter key or the send button.
     * Responsible for sending the message to the server.
     * @see ActionListener
     * @see PrintWriter
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        message = outgoingMessage.getText(); // get the text in the message box
        timeStamp = new SimpleDateFormat("MMM d HH:mm").format(Calendar.getInstance().getTime()); // gets the current time
        System.out.println(message);
        writer.println(message); // sends the message to the server
        // sets the text in the message box to the username and the message ADDED ON TO the rest of the text
        incomingMessageBox.setText(incomingMessageBox.getText() + "\n" + timeStamp + " [" + username + "]: " + outgoingMessage.getText());
        outgoingMessage.setText(""); // reset the text box

        incomingMessageBox.setCaretPosition(incomingMessageBox.getDocument().getLength()); // scrolls to the bottom of the incoming message box
    }
}
