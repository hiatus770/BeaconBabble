import java.net.*;
import java.io.*;

/**
 * Client class that connects to the server and sends messages to the server.
 */
public class Client {
    private String hostname;
    private int port;
    private String username;
    public String messageHistory = ""; // stores all the messages sent by the client and the server
    private boolean fullDebug = false; // if true, prints out all the debug messages

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void run() {
        try {
            Socket socket = new Socket(hostname, port); // creates a socket and connects it to the specified port number at the specified IP address

            System.out.println("Connected to the chat server");

            ReadThread readThread = new ReadThread(socket, this);
            readThread.start(); // creates a new thread to read messages from the server
            WriteThread writeThread = new WriteThread(socket, this);
            writeThread.start(); // creates a new thread to write messages to the server

        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            if (fullDebug) System.out.println("I/O Error: " + e.getMessage());
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public static void main(String[] args) {
        if (args.length < 2) return;

        String hostname = args[0]; // server host name (ip)
        int port = Integer.parseInt(args[1]); // port number

        Client client = new Client(hostname, port);
        client.run();
    }
}
