import java.net.*;
import java.io.*;

/**
 * This thread is responsible for handling all the communication with a single client.
 * This way, the server can handle multiple clients at the same time.
 * In simple terms, this class represents each user.
 * @author goose
 */
public class UserThread extends Thread {
    private Socket socket;
    private Server server;
    private PrintWriter writer; // writes formatted representations of objects as text output

    public UserThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    /**
     * Runs the user thread. Handles all the interactions the user thread should be able to do with the server.
     */
    public void run() {
        try {
            InputStream input = socket.getInputStream(); // returns an input stream for this socket

            // Reads text from the character-input stream above
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream(); // returns the output stream for the socket
            writer = new PrintWriter(output, true);

            String username = reader.readLine(); // obtains username from the client
            server.addUsername(username, this); // adds the username to the set of usernames and the user object 

            String serverMessage = "New user connected: " + username + ". Welcome!";
            server.broadcast(serverMessage, this); // Broadcasts the newly connected user to all users

            String clientMessage;

            do {
                clientMessage = reader.readLine(); // receives the client message
                serverMessage = "[" + username + "]: " + clientMessage; // formatting client message
                server.broadcast(serverMessage, this); // broadcasts the client message to all users
            } while (!clientMessage.equals("/exit"));

            server.removeUsername(username, this); // removes the username from the set of usernames after they DC
            socket.close(); // close le socket

            serverMessage = username + " has left the room."; // bye bye message
            server.broadcast(serverMessage, this);

        } catch (IOException e) {
            if (Server.fullDebug){
                System.out.println("Error in UserThread: " + e.getMessage()); // uh oh poopy stinky
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints a list of online users to the newly connected user.
     */
    public void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUsernames());
        } else {
            writer.println("No other users connected");
        }
    }

    /**
     * Sends a message from the user to the server for broadcast.
     * @param message The message to be sent, in a String format.
     */
    public void sendMessage(String message) {
        writer.println(message);
    }
}
