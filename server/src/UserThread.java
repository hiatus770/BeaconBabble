import java.net.*;
import java.io.*;

/**
 * This thread is responsible for handling all the communication with a single client.
 * This way, the server can handle multiple clients at the same time.
 * Uses the logger to log all the messages and when a user joins and when the user leaves 
 * In simple terms, this class represents each user.
 * @author goose
 */
public class UserThread extends Thread {
    // Both the socket and the server object are passed during initialization of the userthread object
    private Socket socket;
    private Server server;

    // Message writing object and the timestamp for the message
    private PrintWriter writer;
    private BufferedReader reader;
    MessageLogger logger;

    /**
     *  Userthread constructor, only takes the server socket between the client and it takes the server object to access the server methods
     */
     public UserThread(Socket socket, Server server, MessageLogger logger) {
        this.socket = socket;
        this.server = server;
        this.logger = logger;
    }


    /**
     * Prints a list of online users to the newly connected user.
     */
    public void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected user: " + server.getUsernames());
        } else {
            writer.println("No other users connected.");
        }
    }

    /**
     * Sends a message from the user to the server for broadcast.
     * @param message The message to be sent, in a String format.
     */
    public void sendMessage(String message) {
        writer.println(message);
    }

    /**
     * Runs the user thread. Handles all the interactions the user thread should be able to do with the server.
     * This includes receiving messages from the user, sending messages to the user, and removing the user from the server when they disconnect.
     * @author goose and hiatus
     */
    public void run() {
        try {
            // Setting up the user information before the while loop

            // Reads text from the character-input stream above
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Output stream for the socket which lets the server write to this userthread 
            writer = new PrintWriter(socket.getOutputStream(), true);
            /* TODO: Password verification
            PasswordVerify passwordVerify = new PasswordVerify(new File("out/production/server/resources/password.txt"));

            // Keeps notifying client that password is wrong until it's right
            String password = reader.readLine();
            while (!passwordVerify.verify(password)) {
                writer.println("wrongpassword"); 
                password = reader.readLine();
            }
            writer.println("verified"); // sends verified to the client to let them know they have been verified
            */

            // grabs user mac address
            String username = reader.readLine(); // obtains username from the client

            server.addUsername(username, this); // adds the username to the set of usernames and the user object 

            String serverMessage = "New user connected: " + username + ". Welcome!";
            server.broadcast(serverMessage, this); // Broadcasts the newly connected user to all macAddresses.txt
            
            // Log the information
            logger.log("User " + username + " has connected to the server from " + socket.getInetAddress().getHostAddress());

            // Prints a list of online macAddresses.txt to the newly connected user
            printUsers();

            // String for the client message
            String clientMessage = "";

            // Keeps reading messages from the client until the client sends the /exit signal, this is only sent when closing the window
            while (!clientMessage.equals("/exit")) {
                // Sends message AFTER the thread has received the message, this forces the loop to check for an exit message
                serverMessage = clientMessage; // formatting client message
                server.broadcast(serverMessage, this); // broadcasts the client message to all macAddresses.txt

                // Grabs input from the ReadThread in java client
                clientMessage = reader.readLine(); // receives the client message
                
                // Print the client message to the console
                System.out.println(clientMessage);

                // Log the client message to the logger
                if (!clientMessage.equals("/exit")){
                    // Add ip information
                    logger.log("[IP: " + socket.getInetAddress().getHostAddress() + "] " + clientMessage);
                }

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
                    writer.println("Username changed to " + username + ".");
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
            server.broadcast(serverMessage, this);

        } catch (IOException e) {
            // Catch the error if the user disconnects and print to the server 
            if (server.fullDebug){
                System.out.println("Error in UserThread: " + e.getMessage()); 
                e.printStackTrace();
            }
        }
    }

}
