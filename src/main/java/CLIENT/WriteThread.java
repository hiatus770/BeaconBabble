import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

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
        Console console = System.console(); // returns the console object associated with the current Java application

        String username = console.readLine("\nEnter your name: "); // reads a line of text from the console
        client.setUsername(username);
        writer.println(username); // prints the username to the server

        String text;

        do {
            text = console.readLine("[" + username + "]: "); // reads a line of text from the console
            writer.println(text); // prints the text to the server
        } while (!text.equals("/exit"));

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error writing to server: " + e.getMessage());
        }
    }
}
