import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    String message;


    public PrintWriter writer;
    public BufferedReader reader;

    public Icon icon = new ImageIcon(getClass().getResource("resources/icon.png"));

    public GUI(Socket socket, Client client) throws IOException {
        super(new GridBagLayout());

        this.socket = socket;
        this.client = client;

        message = "";

        writer = new PrintWriter(socket.getOutputStream(), true); // used for writing messages to the server
        reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream())); // used for reading messages from the server
    }

    /**
     * Creates a new frame for the client.
     */
    public void createFrame(GUI gui) {
        createGUIcomponents();
        createMenuBar();

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
        frame.add(gui);
        frame.setVisible(true);
        frame.setJMenuBar(menuBar);

        Image icon = new ImageIcon(getClass().getResource("resources/icon.png")).getImage();
        try {
            frame.setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Could not find icon.png");
        }
    }

    public void createGUIcomponents() {
        incomingMessageBox = new JTextPane();
        incomingMessageBox.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(incomingMessageBox);
        incomingMessages = incomingMessageBox.getStyledDocument();
        serverstyle = incomingMessageBox.addStyle("Server message", null);
        clientstyle = incomingMessageBox.addStyle("Client message", null);
        mystyle = incomingMessageBox.addStyle("My message", null);

        StyleConstants.setForeground(serverstyle, Color.BLUE);
        StyleConstants.setForeground(clientstyle, new Color(53, 0, 0));
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
     * @author goose
     */
    public void createMenuBar() {
        // creates a menu category for the menu bar
        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem changeUsername = new JMenuItem("Change username");
        changeUsername.addActionListener(e -> { // lambda expression for the change username button
            // prompts the user to enter a new username
            client.setUsername(JOptionPane.showInputDialog(frame, "Enter a new username:", "Beacon", JOptionPane.PLAIN_MESSAGE)); 
            // if the user presses cancel, the username is set to null
            if (client.getUsername() == null) {
                client.setUsername("Anonymous");
            } else if (client.getUsername().equals("")) {
                client.setUsername("Anonymous");
            }
            // if the user presses ok, the username is set to the input
            else {
                writer.println("/chgusrnmcd " + client.getUsername());
                writer.flush();
            }
        });

        /*
         * TODO: Implement change server and change port
         */
        JMenuItem changeServer = new JMenuItem("Change server");
        JMenuItem changePort = new JMenuItem("Change port");

        settingsMenu.add(changeUsername);
        settingsMenu.add(changeServer);
        settingsMenu.add(changePort);

        // creates menu bar, adds the settings menu to it
        menuBar = new JMenuBar();
        menuBar.add(settingsMenu);
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
        // Get the current time and format it
        // the time stamp of the message
        String timeStamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());

        // Get what is currently typed in the message box and the timestamp  
        message = "[" + timeStamp + "]" + " <" + client.getUsername() + ">: " + outgoingMessage.getText(); 

        // Print for debugging
        System.out.println(message);
        
        // Write the message to the server socket 
        writer.println(message); 

        // sets the text in the message box to the username and the message ADDED ON TO the rest of the text
        try {
            incomingMessages.insertString(incomingMessages.getLength(), message + "\n", mystyle);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        outgoingMessage.setText(""); // reset the text box

        incomingMessageBox.setCaretPosition(incomingMessageBox.getDocument().getLength()); // scrolls to the bottom of the incoming message box
    } 
}
