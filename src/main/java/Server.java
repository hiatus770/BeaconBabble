import java.net.*;
import java.io.*;
import java.util.*;


public class Server {
    private int port;
    private Set<String> usernames = new HashSet<>();
    private Set<Thread> userThreads = new HashSet<>();

    /**
     * @param port the port number for the server to listen on
     *             (must be between 0 and 65535)
     * @throws IOException if an I/O error occurs when opening the socket
     * @throws IllegalArgumentException if the port parameter is outside the specified range of valid port values (but this likely won't happen)
     * This method is called by the main method to initialize the server
     */
    public void init(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Server is listening on port " + port);
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + port + " or listening for a connection");
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        Server server = new Server();
        server.init(port); // initialize the server

    }
}
