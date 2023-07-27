package org.beacon.server;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.sql.SQLException;

/**
 * This thread is responsible for handling all the communication with a single client.
 * The thread uses the logger to log all the messages and when a user joins and when the user leaves.
 * @author goose
 */
public class UserThread extends Thread {
    // Both the socket and the server object are passed during initialization of the userthread object
    private final Socket socket;
    private final Server server;

    // Message writing object and the timestamp for the message
    private final PrintWriter writer;
    BufferedReader reader;

    String username;

    /**
     *  UserThread constructor, only takes the server socket between the client, and it takes the server object to access the server methods
     * @param socket The socket between the client and the server
     * @throws IOException If the socket is not valid
     */
    public UserThread(Socket socket, Server server) throws IOException {
        // Initialize the socket and the server object
        this.socket = socket;
        this.server = server;
        // Reads text from the character-input stream above
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Output stream for the socket which lets the server write to this user thread
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    /**
     * Sends a message from the user to the server for broadcast.
     * @param message The message to be sent, in a String format.
     */
    public void sendMessage(String message) throws IOException {
        // Writes the message to the output stream
        writer.println(message);
        server.log(String.format("Sent message: %s", message));
    }

    /**
     * Prints a list of online users to the newly connected user.
     * This method is called when a user connects to the server.
     * @author Goose et al.
     */
    public void sendOnlineUsers() throws IOException {
        if (server.hasUsers()) {
            sendMessage("Connected users: " + server.getUsernames());
        } else {
            sendMessage("No other users connected.");
        }
    }

    /**
     * Runs the user thread. Handles all the interactions the user thread should be able to do with the server.
     * This includes receiving messages from the user, sending messages to the user, and removing the user from the server when they disconnect.
     * @author goose and hiatus
     */
    public void run() {
        try {
            // Read request signal
            StringBuilder request = new StringBuilder();
            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                request.append(line);
            }

            server.log("Request: " + request);

            // Verify request signal
            if (!request.toString().startsWith("vbcnclnt")) {
                server.log("Connection blocked; invalid request.");
                socket.close();
                return;
            }
            writer.println("accepted");

            // Read login info
            // Ask registration loop
            boolean askRegister;
            do {
                String loginType = reader.readLine();
                if (loginType.equals("cancel")) {
                    server.log(String.format("User <%s> has cancelled.", username));
                    socket.close();
                    return;
                }

                if (loginType.equals("login")) { // login
                    askRegister = checkLogin();
                } else { // registration
                    askRegister = checkRegistration();
                }
            } while (askRegister);

            server.broadcast("User <" + username + "> has connected.", this);
            sendOnlineUsers();
            sendHelpMessage();

            String clientMessage = "";
            while (!clientMessage.equals("/exit")) {

                clientMessage = reader.readLine();
                // command handling
                switch (clientMessage) {
                    case "/exit" -> {
                        server.removeUser(username, this);
                        server.broadcast("User <%s> has disconnected.".formatted(username), this);
                        socket.close();
                    }
                    case "/online" -> {
                        server.log("User <%s> requested online users.".formatted(username));
                        sendOnlineUsers();
                    }
                    case "/help" -> {
                        server.log("User <%s> requested help menu.".formatted(username));
                        sendHelpMessage();
                    }
                    default -> {
                        server.broadcast(String.format("[%s] <%s> %s", server.getTimestamp(), username, clientMessage), this);
                        server.log(String.format("[%s] <%s>: %s", socket.getInetAddress().getHostAddress(), username, clientMessage));
                    }
                }
            }

            server.log(String.format("User <%s> has disconnected.", username));
        } catch (IOException | SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks the registration credentials of the user.
     * @return true if the user presses cancel, false if the user successfully registers.
     * @throws IOException if the input stream is not found.
     * @throws SQLException if the SQL query fails.
     */
    public boolean checkRegistration() throws IOException, SQLException {
        while (true) {
            username = reader.readLine();
            String password = reader.readLine();

            if (username.equals(" ")) {
                return true;
            }

            if (server.registerUser(username, password)) {
                server.addUser(username, this);
                server.log("User registered: " + username);
                sendMessage("goodRegistration");
                return false;
            } else {
                server.log("User registration failed: " + username);
                sendMessage("badRegistration");
            }
        }
    }

    /**
     * Checks the login credentials of the user.
     * @return true if the user presses cancel, false if the user successfully logs in.
     * @throws IOException if the input stream is not found.
     * @throws SQLException if the SQL query fails.
     */
    public boolean checkLogin() throws IOException, SQLException {
        while (true) {
            username = reader.readLine();
            String password = reader.readLine();

            if (username.equals(" ")) {
                return true;
            }

            if (server.checkCredentials(username, password)) {
                server.addUser(username, this);
                server.log("User logged in: " + username);
                sendMessage("goodLogin");
                return false;
            } else {
                server.log("User login failed: " + username);
                sendMessage("badLogin");
            }
        }
    }

    /**
     * Sends a help message to the user.
     * @throws IOException if the input stream is not found.
     * @throws InterruptedException if the thread is interrupted.
     */
    public void sendHelpMessage() throws IOException, InterruptedException {
        sendMessage("/exit - Exits the chat");
        sendMessage("/online - Lists all online users");
        sendMessage("/help - Displays this message");

    }
}


