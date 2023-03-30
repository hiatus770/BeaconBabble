import java.net.*;
import java.io.*;

public class Client {
    private String hostname;
    private int port;
    private String username;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void run() {
        try {
            Socket socket = new Socket(hostname, port); // creates a socket and connects it to the specified port number at the specified IP address

            System.out.println("Connected to the chat server");

            new ReadThread(socket, this).start(); // creates a new thread to read messages from the server
            new WriteThread(socket, this).start(); // creates a new thread to write messages to the server

        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
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

        String hostname = args[0]; // server host name
        int port = Integer.parseInt(args[1]); // port number

        Client client = new Client(hostname, port);
        client.run();
    }
}
