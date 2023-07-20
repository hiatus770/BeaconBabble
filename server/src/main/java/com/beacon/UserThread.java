/*
 * UserThread.java
 * Author: Goose
 * Purpose: This thread is responsible for handling all the communication with a single client.
 * This way, the server can handle multiple clients at the same time.
 */
package com.beacon;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.sql.SQLException;

/**
 * This thread is responsible for handling all the communication with a single client.
 * This way, the server can handle multiple clients at the same time.
 * The thread uses the logger to log all the messages and when a user joins and when the user leaves
 * In simple terms, this class represents each user.
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
        // Output stream for the socket which lets the server write to this userthread
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    /**
     * Sends a message from the user to the server for broadcast.
     * @param message The message to be sent, in a String format.
     */
    public void sendMessage(String message) {
        // Writes the message to the output stream
        writer.println(message);
    }

    /**
     * Prints a list of online users to the newly connected user.
     * This method is called when a user connects to the server.
     * @author Goose et al.
     */
    public void printOnlineUsers() {
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
            while (!(line = reader.readLine()).equals("")) {
                request.append(line);
                System.out.println(line);
            }

            server.log("Request: " + request);
            System.out.println(request);

            // Verify request signal
            if (!request.toString().startsWith("vbcnclnt")) {
                server.log(String.format("User <%s> has sent an invalid request signal, and the connection has been blocked.", username));
                System.out.println("Connection blocked.");
                socket.close();
                return;
            }
            writer.println("accepted");

            // Read login info
            // Ask registration loop
            boolean askRegister = true;
            do {
                String loginType = reader.readLine();
                System.out.println(loginType);
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
            printOnlineUsers();

            String clientMessage = "";
            while (!clientMessage.equals("/exit")) {

                clientMessage = reader.readLine();

                if (clientMessage.equals("/exit")) {
                    server.removeUser(username, this);
                    server.broadcast("User <" + username + "> has disconnected.", this);
                    socket.close();
                } else {
                    server.broadcast(String.format("[%s] <%s> %s", server.getTimestamp(), username, clientMessage), this);
                    System.out.println("message broadcasted");
                    server.log(String.format("[%s] <%s>: %s", socket.getInetAddress().getHostAddress(), username, clientMessage));
                }
            }

            server.log(String.format("User <%s> has disconnected.", username));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
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
            System.out.printf("Username: %s Password: %s%n", username, password);

            if (username.equals(" ")) {
                return false;
            }

            if (server.registerUser(username, password)) {
                server.addUser(username, this);
                sendMessage("goodRegistration");
                return true;
            } else {
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
            System.out.printf("Username: %s Password: %s", username, password);

            if (username.equals(" ")) {
                return true;
            }

            if (server.checkCredentials(username, password)) {
                server.addUser(username, this);
                sendMessage("goodLogin");
                return false;
            } else {
                sendMessage("badLogin");
            }
        }
    }
}


