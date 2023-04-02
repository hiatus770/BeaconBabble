// Acts as the thread that reads the messages from the server and displays it 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Thread responsible for reading messages from the server.
 * @author goose and hiatus
 */
public class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private Client client;

    /**
     * Implements thread responsible for reading messages from the server.
     * @param socket the socket to read from
     * @param client the client that is reading from the server
     * @author goose and hiatus
     */
    public ReadThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream(); // takes in a input stream from the socket
            reader = new BufferedReader(new InputStreamReader(input)); // just reads the input from above
        } catch (IOException e) {
            System.out.println("Error getting input stream: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                String incomingMessage = reader.readLine(); // sets the incoming message to the response from the server
                client.incomingMessageBox.setText(client.incomingMessageBox.getText() + incomingMessage + "\n"); // sets the text of the incoming message box to the incoming message
            } catch (IOException e){
                System.out.println("Error reading from server: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }
}
