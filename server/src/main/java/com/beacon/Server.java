package com.beacon;

import java.net.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
    ServerSocket serverSocket;
    Socket clientSocket;
    Connection connection; // Connection SQL db

    private final Set<String> onlineUsers = new HashSet<>(); // Set of users currently online
    private final Set<UserThread> userThreads = new HashSet<>(); // Set of threads for each user
    static Properties properties = new Properties(); // Server properties

    File logFile;
    FileWriter fileWriter;

    String url, password, username; // SQL connection details

    GUI gui;
    boolean isRunning = false;

    /**
     * Constructor for the server. Initializes the log file and the SQL connection details.
     * @throws IOException if the log file cannot be created
     */
    public Server() throws IOException {
        logFile = new File("src/main/resources/log.txt");
        if (!logFile.createNewFile()) System.out.println("Log file exists at " + logFile.getAbsolutePath());
        fileWriter = new FileWriter(logFile, true);

        url = "jdbc:mysql://localhost:3306/beacon?useSSL=false";
        username = "root";
        password = "!GLVWF*Xu$M5Bj#8&AR^%BPFrpJ4U38bDE2WYKbW";
    }

    /**
     * Runs the server loop.
     * @param port the port to run the server on
     */
    public void run(int port) {
        try {
            gui = new GUI(this);

            serverSocket = new ServerSocket(port);
            log("Server is listening on port " + port);
            System.out.println("Server is listening on port " + port);

            connection = DriverManager.getConnection(url, username, password);
            log("Connected to mySQL database");

            properties.setProperty("hostname", InetAddress.getLocalHost().getHostName());
            isRunning = true;

            // TODO: Provide boolean condition through the closing of the gui window
            while (isRunning) {
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
        String query = "SELECT USERNAME, PASSWORD FROM accounts WHERE USERNAME = \"" + username + "\"";
        System.out.println(query);
        PreparedStatement statement = connection.prepareStatement(query);
        System.out.println(statement.toString());
        ResultSet credentials = statement.executeQuery();
        if (credentials.next()) return Objects.equals(password, credentials.getString("password"));
        else return false;
    }

    // TODO: Make sure the user is not already registered
    /**
     * Registers a new user in the database.
     * @param username the username of the user
     * @param password the password of the user
     */
    public boolean registerUser(String username, String password) throws SQLException {
        System.out.println(username);
        System.out.println(password);
        String queryUsername = "SELECT USERNAME FROM accounts WHERE USERNAME = \"" + username + "\"";
        PreparedStatement queryStatement = connection.prepareStatement(queryUsername);
        ResultSet usernameResult = queryStatement.executeQuery();
        if (usernameResult.next()) {
            System.out.println("Username already exists");
            return false;
        } else {
            String update = "INSERT INTO accounts (USERNAME, PASSWORD) VALUES (\"" + username + "\", \"" + password + "\")";
            System.out.println(update);
            PreparedStatement updateStatement = connection.prepareStatement(update);
            System.out.println(updateStatement.toString());
            int result = updateStatement.executeUpdate();
            System.out.println("Rows affected: " + result);
            return true;
        }
    }

    /**
     * Logs an event to the log file
     * @param event the event to log
     * @throws IOException if the file cannot be written to
     */
    public void log(String event) throws IOException {
        // Log the event with its timestamp
        fileWriter.append(String.format("\n[%s] %s", java.time.LocalDateTime.now().toString(), event));
        gui.console.append(String.format("\n[%s] %s", java.time.LocalDateTime.now().toString(), event));
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
    public void addUser(String username, UserThread userThread) {
        onlineUsers.add(username);
        userThreads.add(userThread);
    }

    /**
     * Removes a username from the ArrayList of usernames.
     * @param username the username of the user to be removed
     */
    public void removeUser(String username, UserThread userThread) {
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

    public String getTimestamp() {
        return new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
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
        server.fileWriter.close();
    }
}
