package com.beacon.client;
import com.beacon.gui.GUI; // Import the GUI class

import java.awt.*; // AWT imports
import java.io.*; // IO imports for reading and writing
import java.net.Socket; // Socket import for connecting to the server
import java.nio.charset.StandardCharsets; // Charset import for UTF-8 encoding
import javax.swing.text.BadLocationException; // BadLocationException import for adding messages to the incomingMessageBox

/**
 * Thread responsible for reading messages from the server.
 * @author Oliver, Matias
 */
public class ReadThread extends Thread {
    private BufferedReader reader;
    private Client client;
    private GUI gui;

    // for the notification tray
    private SystemTray tray;
    private TrayIcon trayIcon;

    /**
     * Implements thread responsible for reading messages from the server.
     * @param socket the socket to read from
     * @param client the client that is reading from the server
     * @author Oliver, Matias
     */
    public ReadThread(Socket socket, Client client, GUI gui) throws IOException {
        this.gui = gui;
        this.client = client;
        // Reads an input stream from the socket with UTF-8 encoding to support emojis
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)); 
    }

    /**
     * Runs the thread.
     * @author Oliver, Matias
     */
    public void run() {
        tray = SystemTray.getSystemTray(); // Initialize a system tray
        trayIcon = new TrayIcon(gui.icon, "Beacon"); // Make a tray icon
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Beacon");
        trayIcon.setImage(gui.icon);

        try {
            tray.add(trayIcon); // Adds the app to the system tray
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        // Thread stops once the user presses the exit button
        while (client.isRunning) { 
            try {
                String incomingMessage = reader.readLine(); // sets the incoming message to the response from the server
                incomingMessage = client.encryptor.decrypt(incomingMessage);
                gui.addMessage(incomingMessage, gui.clientstyle); // Sets the text of the incoming message box to the incoming message
                System.out.println(incomingMessage); // Debugging purposes
                // Checks if the frame is active, sends a notification if it is not
                if (!gui.frame.isActive()) trayIcon.displayMessage("Beacon", incomingMessage, TrayIcon.MessageType.INFO);
            } catch (IOException e){
                System.out.println("Error reading from server: " + e.getMessage());
                e.printStackTrace();
            } catch (BadLocationException e) {
                System.out.println("Error adding message to incomingMessageBox: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
