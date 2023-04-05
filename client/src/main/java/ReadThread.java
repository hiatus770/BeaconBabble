// Acts as the thread that reads the messages from the server and displays it 
import java.awt.*;
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

    // for the notification tray
    private SystemTray tray;
    private Image image;
    private TrayIcon trayIcon;

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
        tray = SystemTray.getSystemTray(); // initialize a system tray
        image = Toolkit.getDefaultToolkit().getImage("icon.png"); // make an icon
        trayIcon = new TrayIcon(image, "Beacon"); // initialize a tray icon
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Beacon");
        trayIcon.setImage(image);

        try {
            tray.add(trayIcon); // adds to the system tray
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                String incomingMessage = reader.readLine(); // sets the incoming message to the response from the server
                client.incomingMessageBox.setText(client.incomingMessageBox.getText() + "\n" + incomingMessage); // sets the text of the incoming message box to the incoming message
                client.incomingMessageBox.setCaretPosition(client.incomingMessageBox.getDocument().getLength()); // scrolls to the bottom of the incoming message box
                //trayIcon.displayMessage("Beacon", incomingMessage, TrayIcon.MessageType.INFO);
                // checks if the frame is active
                if (!client.frame.isActive()) {
                    // send a notification to the user
                    trayIcon.displayMessage("Beacon", incomingMessage, TrayIcon.MessageType.INFO);
                }
            } catch (IOException e){
                System.out.println("Error reading from server: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }
}
