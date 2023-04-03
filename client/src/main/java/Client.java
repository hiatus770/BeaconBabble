import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import javax.swing.*;
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
    private boolean fullDebug = false; // if true, prints out all the debug messages
    public JTextArea incomingMessageBox;
    public JTextField outgoingMessage;

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

    /**
     * Creates a new frame for the client.
     * @param client the client that is creating the frame
     */
    private void createFrame(Client client) {
        JFrame frame = new JFrame("Beacon");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setSize(400, 300);
        frame.add(client);
        frame.setVisible(true);
    }

    /**
     * Runs the client-side code.
     * Contains the socket connection to the server, writing messages to the server, and GUI.
     * @author goose
     */
    public void run() {
        try {
            // Start a socket at the hostname and port
            Socket socket = new Socket(hostname, port); // creates a socket and connects it to the specified port number at the specified IP address

            // Writing
            writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to the chat server");
            incomingMessageBox.setText("Connected to the chat server on address " + hostname + " on port " + port + ".");
            createFrame(this); // creates the window frame for the client
            username = JOptionPane.showInputDialog("Enter a username: ");
            incomingMessageBox.setText(incomingMessageBox.getText() + "\nWelcome to the chat, " + username + "!\n");
            writer.println(username); // sending the username to the server

            // Start the read thread for the program, this will add any received messages to the incomingMessageBox
            ReadThread readThread = new ReadThread(socket, this);
            readThread.start(); // creates a new thread to read messages from the server
        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            if (fullDebug) System.out.println("I/O Error: " + e.getMessage());
        }
    }

    /**
     * Main method for the client, handles for the command line arguments and initializing the client object
     * @param args
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        if (args.length < 2) return; // ill figure out args for gradle later

        String hostname = args[0]; // server host name (ip)
        int port = Integer.parseInt(args[1]); // port number

        Client client = new Client(hostname, port);
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
        message = outgoingMessage.getText();
        System.out.println(message);
        writer.println(message); // sends the message to the server
        // sets the text in the message box to the username and the message ADDED ON TO the rest of the text
        incomingMessageBox.setText(incomingMessageBox.getText() + "[" + username + "]: " + outgoingMessage.getText() + "\n");
        outgoingMessage.setText("");

        incomingMessageBox.setCaretPosition(incomingMessageBox.getDocument().getLength());
    }
}
