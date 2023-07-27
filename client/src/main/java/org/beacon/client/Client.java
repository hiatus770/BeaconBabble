package org.beacon.client;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;

public class Client extends Application {
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    DialogBoxes dialogBoxes;
    File logFile;
    FileWriter fileWriter;

    String username;

    boolean isRunning, debugMode = false;

    /**
     * Main method for the client.
     * @param stage the stage for the client
     * @throws IOException if the client cannot connect to the server
     */
    @Override
    public void start(Stage stage) throws IOException {
        // check/create log file
        logFile = new File("src/main/resources/log.txt");
        if (!logFile.createNewFile()) {
            fileWriter = new FileWriter(logFile, true);
            log("Log file exists at " + logFile.getAbsolutePath());
        }

        log("Starting client...");

        Optional<Pair<String, Integer>> connectionInfo;
        dialogBoxes = new DialogBoxes(this);

        // Attempting to connect to a server
        boolean connectionResult;
        do {
            connectionInfo = dialogBoxes.connectDialog();
            if (connectionInfo.isPresent()) {
                // Avoids the application crashing if the user inputs a port that is out of range
                connectionResult = connect(connectionInfo.get());
                if (connectionResult) {
                    run(connectionInfo.get().getKey(), connectionInfo.get().getValue());
                }
            } else {
                log("Cancelling...");
                return;
            }
        } while (!connectionResult);
    }

    /**
     * Runs the client.
     *
     * @param hostname the hostname of the server
     * @param port     the port of the server
     */
    public void run(String hostname, int port) throws IOException {
        isRunning = true;
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Verification
        writer.println("vbcnclnt " + socket.getInetAddress() + "\n");

        // wait for signal back
        String verification = reader.readLine();
        if (!verification.equals("accepted")) {
            log("Server verification failed");
            dialogBoxes.IOConnectionAlert(hostname, port);
            return;
        }

        // Account login
        boolean askRegister;
        do {
            log("Asking user to login or register...");
            int registerStatus = dialogBoxes.askRegister();
            if (registerStatus == 0) askRegister = handleLoginQuery();
            else if (registerStatus == 1) askRegister = handleRegisterQuery();
            else {
                writer.println("cancel");
                socket.close();
                System.exit(0);
                return;
            }
            if (askRegister) log("Registration cancelled");
            else log("Login cancelled");
        } while (askRegister);

        String connectedUsers = reader.readLine();

        ChatWindow chatWindow = new ChatWindow(this);
        ReadThread readThread = new ReadThread(this, chatWindow);
        readThread.start(); // Starts the read thread

        chatWindow.appendServerMessage("Connected to server at " + hostname + " on port " + port + ".\n");
        chatWindow.appendServerMessage(connectedUsers + "\n");
    }

    /**
     * Attempts a connection to the specified server.
     * @return true if the connection was successful, false otherwise.
     */
    public boolean connect(Pair<String, Integer> connectionInfo) {
        if (connectionInfo.getValue() > 65535 || connectionInfo.getValue() < 0) {
            dialogBoxes.invalidPortAlert();
            return false;
        } else {
            try {
                socket = new Socket(connectionInfo.getKey(), connectionInfo.getValue());
            } catch (UnknownHostException e) {
                dialogBoxes.unknownHostAlert(connectionInfo.getKey(), connectionInfo.getValue());
                return false;
            } catch (IOException e) {
                dialogBoxes.IOConnectionAlert(connectionInfo.getKey(), connectionInfo.getValue());
                return false;
            }
        }
        return true;
    }

    /**
     * Logs an event to the log file and prints it to the console.
     * @param event the event to log.
     * @throws IOException if an I/O error occurs when writing to the log file.
     */
    public void log(String event) throws IOException {
        fileWriter.append(String.format("[%s] %s\n", java.time.LocalDateTime.now(), event));
        if (debugMode) System.out.printf("%s\n", event);
    }

    /**
     * Sends an exit signal to the server and switches a boolean to stop the client.
     */
    public void exit() {
        isRunning = false;
        writer.println("/exit");
    }

    /**
     * Returns the username of the client.
     * @return the username of the client.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sends a message to the server.
     * @param message the message to send.
     */
    public void sendMessage(String message) {
        writer.println(message);
    }

    /**
     * Sends a register request to the server and handles the response.
     * @return true if the user cancelled the registration, false otherwise.
     * @throws IOException if an I/O error occurs when sending or receiving.
     */
    public boolean handleRegisterQuery() throws IOException {
        writer.println("register"); // Send register request to server
        String response = "";
        while (!response.equals("goodRegistration")) {
            Optional<Pair<String, String>> registerPair = dialogBoxes.register(); // Get login info from user
            if (registerPair.isPresent()) {
                writer.println(registerPair.get().getKey());
                writer.println(registerPair.get().getValue());
                username = registerPair.get().getKey();
            } else {
                writer.println(" \n ");
                username = null;
                return true;
            }
            response = reader.readLine();
            if (response.equals("badRegistration")) dialogBoxes.badRegistrationAlert(); // If registration failed
        }
        log("Registration successful");
        return false;
    }

    /**
     * Sends a login request to the server and handles the response.
     * @throws IOException if an I/O error occurs when sending or receiving.
     */
    public boolean handleLoginQuery() throws IOException {
        writer.println("login"); // Send login request to server
        String response = "";
        while (!response.equals("goodLogin")) {
            Optional<Pair<String,String>> loginPair = dialogBoxes.login(); // Get login info from user
            if (loginPair.isPresent()) { // If user didn't cancel
                // Send login info to server
                writer.println(loginPair.get().getKey());
                writer.println(loginPair.get().getValue());
                username = loginPair.get().getKey();
            } else { // If user cancelled
                writer.println(" \n ");
                username = null;
                return true; // Return to askRegister dialog
            }
            response = reader.readLine();
            // If login failed
            if (response.equals("badLogin")) {
                dialogBoxes.badLoginAlert();
                log("Login failed");
            }
        }
        return false;
    }

    /**
     * Main method for the client.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
