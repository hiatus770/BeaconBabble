import java.net.*;
import java.io.*;
import java.util.*;

/**
 * This code is generates an instance of user thread whenever a user connects to the server
 * This includes initializing the server, broadcasting messages to all macAddresses.txt, and keeping track of all the usernames.
 * @author goose et al.
 */
public class Server {
    private Set<String> onlineUsers = new HashSet<>(); // Set of all the macAddresses.txt currently online
    private Set<UserThread> userThreads = new HashSet<>(); // Set of all the user threads
    public boolean fullDebug = false; // if true, prints out all the debug messages
    MessageLogger logger = new MessageLogger(); // logger for the server

    // this is only to errors with the MACLogger
    public Server() throws IOException {

    }

    /**
     * This method is called by the main method to initialize the server.
     * It continuously listens for new connections.
     * If there is a new connection, is creates a new UserThread object for the new user.
     * @throws IOException if there is an error with listening on the port
     * @param port the port number for the server to listen on
     *             (must be between 0 and 65535)
     */
    public void run(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port);){
            System.out.println("Server is listening on port " + port);

            // Log the information
            logger.log("Server started on port " + port);

            // Keep on searching for new macAddresses.txt and add them to the userthread
            while (true) {
                Socket clientSocket = serverSocket.accept(); // keeps listening for a connection and if there is, accept connection
                UserThread user = new UserThread(clientSocket, this, logger);
                userThreads.add(user);
                System.out.println("New user has connected from " + clientSocket.getInetAddress().getHostAddress() + " from port " + clientSocket.getPort());
                user.start(); // run le thread
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + port + " or listening for a connection");
            e.printStackTrace();
        }
    }

    /**
     * This method is called by the UserThread class to send a message to all macAddresses.txt.
     * The method loops through all the macAddresses.txt in the ArrayList of user threads and sends the message to each user.
     * @param message the message to be sent
     * @param thread the thread that sent the message
     */
    public void broadcast(String message, UserThread thread) {
        for (UserThread user : userThreads) {
            if (user != thread) { // This is so that we don't send the message back to the user that sent it
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
        onlineUsers.add(username);
        userThreads.add(userThread);
    }

    /**
     * This method is called by the UserThread class to remove a user
     * and their respective user thread from the ArrayList of usernames and user threads.
     * @param username the username of the user to be removed
     */
    public void removeUsername(String username, UserThread userThread) {
        onlineUsers.remove(username);
        userThreads.remove(userThread);
    }

    /**
     * This method is called by the UserThread class to check if there are any macAddresses.txt connected to the server.
     * @return true if there are macAddresses.txt connected to the server, false otherwise
     */
    public boolean hasUsers() {
        return !this.onlineUsers.isEmpty();
    }

    /**
     * This method is called by the UserThread class to get a list of all the usernames connected to the server.
     * @return the set containing all the usernames connected to the server
     * @see Set
     * @implNote The set return type only works because UserThread is using PrintWriter
     */
    public Set<String> getUsernames() {
        return onlineUsers;
    }

    /**
     * Main method for the server. Initializes a server object and calls the run method.
     * @param args the command line arguments for the port number to listen on
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java Server <port number>");
            System.exit(0);
        }
        int port = Integer.parseInt(args[0]);
        Server server = new Server();
        server.run(port); // initialize the server
    }
}
