/*
 * UserThread.java
 * Author: Goose
 * Purpose: This thread is responsible for handling all the communication with a single client.
 * This way, the server can handle multiple clients at the same time.
 */
package com.beacon;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.sql.SQLException;

/**
 * This thread is responsible for handling all the communication with a single client.
 * This way, the server can handle multiple clients at the same time.
 * The thread uses the logger to log all the messages and when a user joins and when the user leaves
 * In simple terms, this class represents each user.
 * @author goose
 */
public class UserThread extends Thread {
    // Both the socket and the server object are passed during initialization of the userthread object
    private Socket socket;
    private Server server;

    // Message writing object and the timestamp for the message
    private PrintWriter writer;
    BufferedReader reader;

    /**
     *  Userthread constructor, only takes the server socket between the client and it takes the server object to access the server methods
     * @param socket The socket between the client and the server
     * @throws IOException
     */
    public UserThread(Socket socket, Server server) throws IOException {
        // Initialize the socket and the server object
        this.socket = socket;
        this.server = server;
        // Reads text from the character-input stream above
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Output stream for the socket which lets the server write to this userthread
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }


    /**
     * Prints a list of online users to the newly connected user.
     * This method is called when a user connects to the server.
     * @author Goose et al.
     */
    public void printOnlineUsers() {
        if (server.hasUsers()) {
            sendMessage("Connected users: " + server.getUsernames());
        } else {
            sendMessage("No other users connected.");
        }
    }

    /**
     * Sends a message from the user to the server for broadcast.
     * @param message The message to be sent, in a String format.
     */
    public void sendMessage(String message) {
        // Writes the message to the output stream
        writer.println(message);
    }

    public boolean verifyPassword(String password) {
        return Server.properties.getProperty("password").equals(password);
    }

    /**
     * Runs the user thread. Handles all the interactions the user thread should be able to do with the server.
     * This includes receiving messages from the user, sending messages to the user, and removing the user from the server when they disconnect.
     * @author goose and hiatus
     */
    public void run() {
        // TODO:
        // 1. Read the username and password from the client socket
        // 2. Verify the username and password
        // 3. If the username and password are correct, add the username to the list and send a signal to allow the connection
        // 4. If the username and password are incorrect, send a signal to deny the connection
        try {
            // Read login info
            String loginType = reader.readLine();
            System.out.println(loginType);
            if (loginType.equals("login")) { // login
                boolean loginState = false;
                while (!loginState) {
                    String username = reader.readLine();
                    String password = reader.readLine();
                    System.out.println("Username: " + username + " Password: " + password);
                    if (server.checkCredentials(username, password)) {
                        server.addUsername(username, this);
                        sendMessage("goodLogin");
                        loginState = true;
                    } else {
                        sendMessage("badLogin");
                    }
                }
            } else { // registration
                // TODO: check if user already exists
                String username = reader.readLine();
                String password = reader.readLine();
                System.out.println("Username: " + username + " Password: " + password);
                server.registerUser(username, password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


