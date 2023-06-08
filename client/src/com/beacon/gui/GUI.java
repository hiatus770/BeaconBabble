package com.beacon.gui;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.beacon.client.Client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * GUI class for the client.
 * Inherits from JPanel and implements ActionListener.
 * @see JPanel for more information on JPanel.
 * @see ActionListener for more information on ActionListener.
 * @author Oliver, Matias 
 */
public class GUI extends JPanel implements ActionListener{
    public Socket socket;
    public Client client;

    public JTextPane incomingMessageBox;
    public JTextField outgoingMessage;
    public JMenuBar menuBar;
    public JFrame frame;
    public JPanel panel;
    public JButton sendButton;
    public JScrollPane scrollPane;

    public StyledDocument incomingMessages;
    public Style serverstyle;
    public Style clientstyle;
    public Style mystyle;

    private String message;

    private PrintWriter writer;

    public Image icon;

    // TODO: Add a method to make setting messages in the incomingMessageBox easier

    /**
     * Constructor for the GUI class. 
     * Creates all the components for the window.
     * @param socket
     * @param client
     * @throws IOException
     */
    public GUI(Socket socket, Client client) throws IOException {
        super(new GridBagLayout());
        this.socket = socket;
        this.client = client;
        message = ""; // initialize message to empty string
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        icon = new ImageIcon(getClass().getResource("resources/icon.png")).getImage();
    }

    /**
     * Creates a frame for the client.
     * @author Oliver
     */
    public void createFrame(GUI gui) {
        createGUIcomponents();
        createMenuBar();

        frame = new JFrame("Beacon");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // If the user closes the window then send the exit signal to the server 
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                client.isRunning = false;
                writer.println(client.encryptor.encrypt("/exit"));
                System.out.println("Exiting!");
            }
        });

        frame.setResizable(true); // allow frame to be resized
        frame.setSize(400, 300); // set dimensions
        frame.add(gui);
        frame.setVisible(true);
        frame.setJMenuBar(menuBar);

        try {
            frame.setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Could not find icon.png");
        }
    }

    /**
     * Creates GUI components for the client.
     * @author Oliver
     */
    public void createGUIcomponents() {
        incomingMessageBox = new JTextPane();
        incomingMessageBox.setEditable(false);
        incomingMessageBox.setEditorKit(new WrapEditorKit());
        JScrollPane scrollPane = new JScrollPane(incomingMessageBox);
        incomingMessages = incomingMessageBox.getStyledDocument();

        serverstyle = incomingMessageBox.addStyle("Server message", null);
        clientstyle = incomingMessageBox.addStyle("Client message", null);
        mystyle = incomingMessageBox.addStyle("My message", null);

        StyleConstants.setForeground(serverstyle, Color.BLUE);
        StyleConstants.setForeground(clientstyle, Color.gray);
        StyleConstants.setForeground(mystyle, Color.BLACK);
        
        outgoingMessage = new JTextField();
        outgoingMessage.addActionListener(this);

        sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(70, outgoingMessage.getPreferredSize().height));
        sendButton.addActionListener(this);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.RELATIVE;
        add(outgoingMessage, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        c.weightx = 0;
        add(sendButton, c);

         // constraint properties for the incoming message box
        c.gridy = 0; // sets the x position of the component to the first grid spot
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);
    }

    /**
     * Creates the menu bar for the client.
     * @author Oliver
     */
    public void createMenuBar() {
        // creates a menu category for the menu bar
        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem changeUsername = new JMenuItem("Change username");
        changeUsername.addActionListener(e -> { // lambda expression for the change username button
            // prompts the user to enter a new username
            String oldUsername = client.getUsername();
            client.setUsername(JOptionPane.showInputDialog(frame, "Enter a new username:", "Beacon", JOptionPane.PLAIN_MESSAGE)); 
            // if the user presses cancel, the username is set to null
            if (client.getUsername().trim().equals("")) {
                client.setUsername(oldUsername);
            } else if (client.getUsername().length() > 20) {
                client.setUsername(client.getUsername().substring(0, 20));
            }
            // if the user presses ok, the username is set to the input
            else {
                writer.println(client.encryptor.encrypt("/chgusrnmcd " + client.getUsername()));
                writer.flush();
            }
        });

        settingsMenu.add(changeUsername);

        // creates menu bar, adds the settings menu to it
        menuBar = new JMenuBar();
        menuBar.add(settingsMenu);
    }

    /**
     * Adds a message to the incoming message box.
     * @param message the message to be added
     * @param style the colour style of the message
     * @throws BadLocationException if the location specified is invalid
     */
    public void addMessage(String message, Style style) throws BadLocationException {
        incomingMessages.insertString(incomingMessages.getLength(), message + "\n", style);
        incomingMessageBox.setCaretPosition(incomingMessageBox.getDocument().getLength());
    }

    /**
     * Sends a message to the server.
     * @param message the message to be sent
     */
    public void sendMessage(String message) {
        writer.println(message);
    }

    /**
     * Processes the event when the user presses the enter key or the send button.
     * Responsible for sending the message including the user data and the timestamp to the server.
     * This method is only identified separately because the same method is used for both the enter key and the send button.
     * Otherwise, this would have been a lambda function, similar to the one used for the menu bar.
     * @see ActionListener
     * @see PrintWriter
     * @see SimpleDateFormat
     * @param e Event passed from the action listener
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Formatting the current time to be displayed in the message
        String timeStamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());

        // Format and package the message to be sent to the server
        // Checks if the message is empty or not
        if (outgoingMessage.getText().trim().length() > 0) { // Checks i
            if (outgoingMessage.getText().length() > 1000) message = "[" + timeStamp + "]" + " <" + client.getUsername() + ">: " + outgoingMessage.getText().substring(0, 1000) + "..."; 
            else message = "[" + timeStamp + "]" + " <" + client.getUsername() + ">: " + outgoingMessage.getText(); 

            System.out.println(message); // Print for debugging
            sendMessage(client.encryptor.encrypt(message)); // Send the message to the server
            outgoingMessage.setText(""); // Reset the message input box
            try {
                addMessage(message, mystyle); // Add the message to the incoming message box
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }
    } 
}
