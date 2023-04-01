import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.util.Scanner;

/**
 * Client class that connects to the server and sends messages to the server.
 */
public class Client extends JPanel implements ActionListener {
    private String hostname;
    private int port;
    private String username;
    public String message; // stores the message sent by the client
    private PrintWriter writer;
    private boolean fullDebug = false; // if true, prints out all the debug messages
    public boolean sendMessage = false;
    public JTextArea incomingMessageBox;
    public String incomingMessage;
    public JTextField outgoingMessage;

    public Client(String hostname, int port) {
        super(new GridBagLayout());

        message = "";

        this.hostname = hostname;
        this.port = port;

        incomingMessageBox = new JTextArea();
        incomingMessageBox.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(incomingMessageBox);

        outgoingMessage = new JTextField();
        outgoingMessage.addActionListener(this);

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.SOUTH;
        add(outgoingMessage, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 4.0;
        c.weighty = 4.0;
        add(scrollPane, c);
    }

    private void createFrame(String hostname, int port, Client client) {
        JFrame frame = new JFrame("Beacon");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setSize(400, 300);

        frame.add(client);

        frame.setVisible(true);
    }

    public void run() {
        try {
            Socket socket = new Socket(hostname, port); // creates a socket and connects it to the specified port number at the specified IP address
            writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to the chat server");
            createFrame(hostname, port, this);
            username = JOptionPane.showInputDialog("Enter a username: ");
            incomingMessageBox.setText("Welcome to the chat, " + username + "!\n");
            writer.println(username); // sending the username to the server

            ReadThread readThread = new ReadThread(socket, this);
            readThread.start(); // creates a new thread to read messages from the server
        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            if (fullDebug) System.out.println("I/O Error: " + e.getMessage());
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        if (args.length < 2) return; // ill figure out args for gradle later

        String hostname = args[0]; // server host name (ip)
        int port = Integer.parseInt(args[1]); // port number

        Client client = new Client(hostname, port);
        client.run();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        message = outgoingMessage.getText();
        System.out.println(message);
        writer.println(message); // sends the message to the server
        incomingMessageBox.setText(incomingMessageBox.getText() + "[" + username + "]: " + outgoingMessage.getText() + "\n");
        outgoingMessage.setText("");

        incomingMessageBox.setCaretPosition(incomingMessageBox.getDocument().getLength());
    }
}
