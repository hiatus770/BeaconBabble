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
    MessageLogger logger;
    BufferedReader reader;
    Encryptor encryptor;

    /**
     *  Userthread constructor, only takes the server socket between the client and it takes the server object to access the server methods
     * @param socket 
     * @throws IOException
     */
     public UserThread(Socket socket, Server server, MessageLogger logger) throws IOException {
        // Initialize the socket and the server object
        this.socket = socket;
        this.server = server;
        this.logger = logger;
        // Reads text from the character-input stream above
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Output stream for the socket which lets the server write to this userthread 
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        // Encryptor
        encryptor = new Encryptor(Server.properties.getProperty("password"));
    }


    /**
     * Prints a list of online users to the newly connected user.
     * This method is called when a user connects to the server.
     * @throws IOException
     * @throws InterruptedException
     * @author Goose et al.
     */
    public void printUsers() {
        if (server.hasUsers()) {
            sendMessage(encryptor.encrypt("Connected users: " + server.getUsernames()));
        } else {
            sendMessage(encryptor.encrypt("No other users connected."));
        }
    }

    /**
     * Sends a message from the user to the server for broadcast.
     * @param message The message to be sent, in a String format.
     * @throws IOException
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
        try {
            // Password verification
            while (!verifyPassword(reader.readLine())) {
                sendMessage("incorrectpassword");
                System.out.println("Incorrect password");
            }
            sendMessage("correctpassword");

            String username = reader.readLine(); // obtains username from the client

            username = encryptor.decrypt(username);

            server.addUsername(username, this); // adds the username to the set of usernames and the user object 

            String serverMessage = "New user connected: " + username + ". Welcome!";
            server.broadcast(encryptor.encrypt(serverMessage), this); 
            
            // Log the information
            logger.log("User " + username + " has connected to the server from " + socket.getInetAddress().getHostAddress());

            // Prints a list of online users to the newly connected user
            printUsers();

            // String for the client message
            String clientMessage = "";

            // Keeps reading messages from the client until the client sends the /exit signal, this is only sent when closing the window
            while (!clientMessage.equals("/exit")) {
                // Sends message AFTER the thread has received the message, this forces the loop to check for an exit message
                server.broadcast(encryptor.encrypt(clientMessage), this); 
                // Waits for a signal/message from the client
                clientMessage = encryptor.decrypt(reader.readLine()); 
                // Print the client message to the console
                System.out.println(clientMessage);

                // As long as the message is not the exit signal, log the client message to the logger
                if (!clientMessage.equals("/exit")){
                    // Add ip information
                    logger.log("[IP: " + socket.getInetAddress().getHostAddress() + "] " + clientMessage);
                }

                if (clientMessage.contains("/users")) printUsers();   

                // checks if the user changed their username
                if (clientMessage.contains("/chgusrnmcd")) {
                    // Get the new username from the client message
                    String newUsername = clientMessage.substring(12);
                    // Remove the old username from the server
                    server.removeUsername(username, this);
                    // Add the new username to the server
                    server.addUsername(newUsername, this);

                    // Set the username to the new username
                    String oldUsername = username;
                    username = newUsername;

                    // Send a message to the client that the username has been changed
                    sendMessage(encryptor.encrypt("Username changed to " + username + "."));
                    clientMessage = oldUsername + " has changed their username to " + username + ".";
                }
            }

            // Log the information including the IP address
            logger.log("User " + username + " has disconnected from the server at " + socket.getInetAddress().getHostAddress());    
            System.out.println("User " + username + " has disconnected from the server at " + socket.getInetAddress().getHostAddress());

            // Removes the user and closes the socket from the server
            server.removeUsername(username, this); 
            socket.close(); 

            // Sends a message to all macAddresses.txt that the user has left the room before exiting
            serverMessage = username + " has left the room."; 
            server.broadcast(encryptor.encrypt(serverMessage), this);

        } catch (IOException e) {
            // Catch the error if the user disconnects and print to the server 
            if (server.fullDebug){
                System.out.println("Error in UserThread: " + e.getMessage()); 
                e.printStackTrace();
            }
        }
    }
}