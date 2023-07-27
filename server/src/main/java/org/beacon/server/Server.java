package org.beacon.server;

import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
    ServerSocket serverSocket;
    Socket clientSocket;

    private final Set<String> onlineUsers = new HashSet<>(); // Set of users currently online
    private final Set<UserThread> userThreads = new HashSet<>(); // Set of threads for each user
    private final HashMap<String, String> accounts = new HashMap<>(); // HashMap of accounts
    static Properties properties = new Properties(); // Server properties

    File logFile, accountFile;
    FileWriter logFileWriter, accountFileWriter;
    BufferedReader accountFileReader;

    GUI gui;
    boolean isRunning = false;

    /**
     * Server constructor. Creates a GUI console for the server and initializes several files and file readers/writers.
     * @throws IOException if the log file cannot be created
     */
    public Server() throws IOException {
        gui = new GUI(this);

        // check/create log file
        logFile = new File("src/main/resources/log.txt");
        logFileWriter = new FileWriter(logFile, true);
        if (!logFile.createNewFile()) {
            log("Log file exists at " + logFile.getAbsolutePath());
            log("Log file writer initialized.");
        } else {
            log("Log file does not exist, creating at " + logFile.getAbsolutePath());
            log("Log file writer initialized.");
        }

        // check/create file for account management
        accountFile = new File("src/main/resources/accounts.txt");
        accountFileReader = new BufferedReader(new FileReader(accountFile));
        accountFileWriter = new FileWriter(accountFile, true);
        if (!accountFile.createNewFile()) {
            log("Account file exists at " + accountFile.getAbsolutePath());
            log("Account file writer initialized.");
        } else {
            log("Account file does not exist, creating at " + accountFile.getAbsolutePath());
            log("Account file writer initialized.");
        }

        while (accountFileReader.ready()) {
            String line = accountFileReader.readLine();
            accounts.put(line.split(":")[0], line.split(":")[1]);
        }
    }

    /**
     * Runs the server loop.
     * @param port the port to run the server on
     */
    public void run(int port) {
        try {
            serverSocket = new ServerSocket(port);
            log("Server is listening on port " + port);

            properties.setProperty("hostname", InetAddress.getLocalHost().getHostName());
            isRunning = true;

            while (isRunning) {
                clientSocket = serverSocket.accept(); // Listens and accepts a new connection
                UserThread userThread = new UserThread(clientSocket, this); // Creates a new thread for the user
                userThreads.add(userThread); // Add the new user to the list of users
                log("New user connected from " + clientSocket.getInetAddress().getHostAddress() + " on port " + clientSocket.getPort());
                userThread.start();
            }
            log("Closing server...");
            serverSocket.close();
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks whether the credentials provided match a user in the database.
     * @param username the username to check
     * @param password the password to check
     * @return whether the credentials match a user in the database
     */
    public boolean checkCredentials(String username, String password) throws IOException {
        if (accounts.containsKey(username)) {
            return accounts.get(username).equals(password);
        }
        return false;
    }

    /**
     * Registers a new user in the accounts file.
     * @param username the username of the user
     * @param password the password of the user
     * @return whether the user was successfully registered
     */
    public boolean registerUser(String username, String password) throws IOException {
        // check if username exists already
        if (accounts.containsKey(username)) {
            return false;
        } else {
            accountFileWriter.append(String.format("%s:%s\n", username, password));
            accounts.put(username, password);
            accountFileWriter.flush();
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
        logFileWriter.append(String.format("\n[%s] %s", java.time.LocalDateTime.now(), event));
        gui.console.append(String.format("\n[%s] %s", java.time.LocalDateTime.now(), event));
    }

    /**
     * This method is called by the UserThread class to send a message to all macAddresses.txt.
     * The method loops through all the macAddresses.txt in the ArrayList of user threads and sends the message to each user.
     * @param message the message to be sent
     * @param thread the thread that sent the message
     */
    public void broadcast(String message, UserThread thread) throws IOException {
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
        server.logFileWriter.close();
    }
}
