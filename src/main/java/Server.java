import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Handles all the server duties.
 * This includes initializing the server, broadcasting messages to all users, and keeping track of all the usernames.
 * @author goose
 */
public class Server {
    private int port;
    private Set<String> usernames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();

    /**
     * This method is called by the main method to initialize the server
     * @param port the port number for the server to listen on
     *             (must be between 0 and 65535)
     * @throws IOException if an I/O error occurs when opening the socket
     * @throws IllegalArgumentException if the port parameter is outside the specified range of valid port values (but this likely won't happen)
     */
    public void run(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // keeps listening for a connection and if there is, accept connection
                System.out.println("New user connected");

                UserThread user = new UserThread(clientSocket, this);
                userThreads.add(user);
                user.start(); // run le thread
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + port + " or listening for a connection");
            e.printStackTrace();
        }
    }

    /**
     * This method is called by the UserThread class to send a message to all users.
     * The method loops through all the users in the ArrayList of user threads and sends the message to each user.
     * @param message the message to be sent
     * @param thread the thread that sent the message
     */
    public void broadcast(String message, UserThread thread) {
        for (UserThread user : userThreads) {
            if (user != thread) {
                user.sendMessage(message);
            }
        }
    }

    /**
     * This method is called by the UserThread class to add a user
     * and their respective user thread from the ArrayList of usernames and user threads.
     * @param username the username of the user to be removed
     */
    public void addUsername(String username, UserThread userThread) {
        usernames.add(username);
        userThreads.add(userThread);
    }

    /**
     * This method is called by the UserThread class to remove a user
     * and their respective user thread from the ArrayList of usernames and user threads.
     * @param username the username of the user to be removed
     */
    public void removeUsername(String username, UserThread userThread) {
        usernames.remove(username);
        userThreads.remove(userThread);
    }

    /**
     * This method is called by the UserThread class to check if there are any users connected to the server.
     * @return true if there are users connected to the server, false otherwise
     */
    public boolean hasUsers() {
        return !this.usernames.isEmpty();
    }

    /**
     * This method is called by the UserThread class to get a list of all the usernames connected to the server.
     * @return the set containing all the usernames connected to the server
     * @see Set
     * @implNote The set return type only works because UserThread is using PrintWriter
     */
    public Set getUsernames() {
        return usernames;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Server <port number>");
            System.exit(0);
        }

        int port = Integer.parseInt(args[0]);
        Server server = new Server();
        server.run(port); // initialize the server
    }
}
