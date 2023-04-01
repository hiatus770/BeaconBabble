import java.io.*;
import javax.swing.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Thread responsible for writing messages to the server.
 * @author goose and hiatus
 */
public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private Client client;

    /**
     * Implements thread responsible for writing messages to the server.
     * @param socket the socket to write to
     * @param client the client that is writing to the server
     * @author goose and hiatus
     */
    public WriteThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream(); // returns the output stream for the socket
            writer = new PrintWriter(output, true);
        } catch (IOException e) {
            System.out.println("Error getting output stream: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        while (client.message != "/exit") {
            if (client.sendMessage) {
                writer.println(client.message); // sends the message to the server
                System.out.println("message sent");
                client.sendMessage = false;
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error writing to server: " + e.getMessage());
        }
    }
}
