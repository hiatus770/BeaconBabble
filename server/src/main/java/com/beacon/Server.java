package com.beacon;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Server {
    ServerSocket serverSocket; // The server socket
    Connection connection; // The connection to the account database

    private Set<String> onlineUsers = new HashSet<>(); // Set of users currently online
    private Set<UserThread> userThreads = new HashSet<>(); // Set of each thread for each user
    static Properties properties = new Properties(); // Server properties
    File logFile;

    String url, password, username; // SQL connection details

    TextArea console = new TextArea(); // The console for the server
    TextField commandLine = new TextField(); // The command line for the server

    //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // For reading input from the console
    Socket clientSocket;

    public Server() throws IOException {
        logFile = new File("src/main/resources/log.txt");
        if (logFile.exists()) System.out.println("Log file exists at " + logFile.getAbsolutePath());
        else logFile.createNewFile();

        url = "jdbc:mysql://localhost:3306/beacon?useSSL=false";
        username = "root";
        password = "!GLVWF*Xu$M5Bj#8&AR^%BPFrpJ4U38bDE2WYKbW";
    }

    public void run(int port) {
        try {
            serverSocket = new ServerSocket(port);
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Server is listening on port " + port);
            log("Server is listening on port " + port);
            log("Connected to mySQL database");

            properties.setProperty("hostname", InetAddress.getLocalHost().getHostName());

            // TODO: Provide boolean condition through the closing of the gui window
            while (true) {
                clientSocket = serverSocket.accept(); // Listens and accepts a new connection
                UserThread userThread = new UserThread(clientSocket, this); // Creates a new thread for the user
                userThreads.add(userThread); // Add the new user to the list of users
                System.out.println("New user connected from " + clientSocket.getInetAddress().getHostAddress() + " on port " + clientSocket.getPort());
                log("New user connected from " + clientSocket.getInetAddress().getHostAddress() + " on port " + clientSocket.getPort());
                userThread.start();
            }

            //serverSocket.close();
            //connection.close();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** // TODO implement the bottom two methods
     * Retrieves a username from the database of accounts.
     * @param id the id of the user
     * @return the username of the user
     */
    public String queryUsername(int id) throws SQLException {
        ResultSet username = connection.createStatement().executeQuery("SELECT username FROM accounts WHERE id = " + id);
        return username.getString("username");
    }

    /**
     * Retrieves a password from the database of accounts.
     * @param id the id of the user
     * @return the password of the user
     */
    public String queryPassword(int id) {
        return null;
    }

    /**
     * Checks whether the credentials provided match a user in the database.
     * @param username the username to check
     * @param password the password to check
     * @return whether the credentials match a user in the database
     */
    public boolean checkCredentials(String username, String password) throws SQLException {
        ResultSet credentials = connection.createStatement().executeQuery("SELECT username, password FROM accounts WHERE username = " + username);
        return Objects.equals(password, credentials.getString("password"));
    }

    public void registerUser(String username, String password) throws SQLException {
        connection.createStatement().executeUpdate("INSERT INTO accounts (username, password) VALUES (" + username + ", " + password + ")");
    }

    /**
     * Creates a rudimentary GUI for the server
     */
    public void createGUI() {
        JFrame frame = new JFrame("Beacon server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setVisible(true);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        frame.add(panel);
    }

    /**
     * Logs an event to the log file
     * @param event the event to log
     * @throws IOException if the file cannot be written to
     */
    public void log(String event) throws IOException {
        FileWriter fileWriter = new FileWriter(logFile, true);
        // Log the event with its timestamp
        fileWriter.append("\n[").append(java.time.LocalDateTime.now().toString()).append("] ").append(event);
        fileWriter.close();
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
     * Adds a new username to the ArrayList of usernames.
     * @param username the username of the user to be removed
     */
    public void addUsername(String username, UserThread userThread) {
        onlineUsers.add(username);
        userThreads.add(userThread);
    }

    /**
     * Removes a username from the ArrayList of usernames.
     * @param username the username of the user to be removed
     */
    public void removeUsername(String username, UserThread userThread) {
        onlineUsers.remove(username);
        userThreads.remove(userThread);
    }

    /**
     * Checks if there are users connected to the server.
     * @return true if there are users connected to the server, false otherwise
     */
    public boolean hasUsers() {
        return !this.onlineUsers.isEmpty();
    }

    /**
     * Returns a Set of usernames connected to the server.
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
        Server server = new Server();
        properties.load(new FileInputStream("src/main/resources/server.properties"));
        int port = properties.getProperty("port") != null ? Integer.parseInt(properties.getProperty("port")) : 8080;
        server.run(port); // initialize the server
    }
}
