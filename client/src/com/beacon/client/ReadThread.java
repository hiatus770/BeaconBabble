package com.beacon.client;
// Acts as the thread that reads the messages from the server and displays it 
import javax.swing.text.BadLocationException;

import com.beacon.gui.GUI;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Thread responsible for reading messages from the server.
 * @author goose and hiatus
 */
public class ReadThread extends Thread {
    private BufferedReader reader;
    private GUI gui;

    // for the notification tray
    private SystemTray tray;
    private TrayIcon trayIcon;

    /**
     * Implements thread responsible for reading messages from the server.
     * @param socket the socket to read from
     * @param client the client that is reading from the server
     * @author goose and hiatus
     */
    public ReadThread(Socket socket, Client client, GUI gui) {
        this.gui = gui;
 
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)); // Reads an input stream from the socket
        } catch (IOException e) {
            System.out.println("Error getting input stream: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Runs the thread.
     * @author goose and hiatus
     */
    public void run() {
        tray = SystemTray.getSystemTray(); // initialize a system tray
        trayIcon = new TrayIcon(gui.icon, "Beacon"); // initialize a tray icon
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Beacon");
        trayIcon.setImage(gui.icon);

        try {
            tray.add(trayIcon); // adds to the system tray
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        while (true) { // thread stops once the user presses the exit button
            try {
                String incomingMessage = reader.readLine(); // sets the incoming message to the response from the server
                // sets the text of the incoming message box to the incoming message
                gui.incomingMessages.insertString(gui.incomingMessages.getLength(), incomingMessage + "\n", gui.clientstyle); 
                // scrolls to the bottom of the incoming message box
                gui.incomingMessageBox.setCaretPosition(gui.incomingMessageBox.getDocument().getLength()); 
                System.out.println(incomingMessage); 

                // checks if the frame is active
                if (!gui.frame.isActive()) {
                    // send a notification to the user
                    trayIcon.displayMessage("Beacon", incomingMessage, TrayIcon.MessageType.INFO);
                }
            } catch (IOException e){
                System.out.println("Error reading from server: " + e.getMessage());
                e.printStackTrace();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
}
