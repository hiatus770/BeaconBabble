package com.beacon.client;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Properties;

/* TODO:
    1. setup closing signal to the server
    2. setup welcome messages
    3. setup different text colours
 */

public class Client extends Application {
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    DialogBoxes dialogBoxes;
    Properties properties;

    String username;

    boolean isRunning;

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Hello");
        String[] connectionInfo; // maybe use this for future stuff
        dialogBoxes = new DialogBoxes(this);
        properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/client.properties"));
        run("localhost", 8000);
        // DO NOT DELETE: logic for checking whether a server connection has been properly established through user defined parameters
        /*do connectionInfo = dialogBoxes.connect();
        while (run(connectionInfo[0], Integer.parseInt(connectionInfo[1])) != 0);*/
    }

    /**
     * Runs the client.
     *
     * @param hostname the hostname of the server
     * @param port     the port of the server
     */
    public void run(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Account login
            boolean askRegister;
            do {
                System.out.println("ask register");
                int registerStatus = dialogBoxes.askRegister();
                if (registerStatus == 0) askRegister = handleLoginQuery();
                else if (registerStatus == 1) askRegister = handleRegisterQuery();
                else {
                    writer.println("cancel");
                    socket.close();
                    System.exit(0);
                    return;
                }
            } while (askRegister);

            String connectedUsers = reader.readLine();

            ChatWindow chatWindow = new ChatWindow(this);
            ReadThread readThread = new ReadThread(this, chatWindow);

            readThread.start(); // Starts the read thread

            chatWindow.appendServerMessage("Connected to server at " + hostname + " on port " + port + ".\n");
            chatWindow.appendServerMessage(connectedUsers + "\n");
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + hostname);
            dialogBoxes.serverConnectionAlert(hostname, port);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + hostname);
            dialogBoxes.serverConnectionAlert(hostname, port);
            System.exit(2);
        }
    }

    /**
     * Sets the username of the client.
     * @param username the new username of the client.
     */
    public void setUsername(String username) {
        this.username = username;
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
                writer.println(registerPair.get().getKey() + "\n" + registerPair.get().getValue()); // Send login info to server
                username = registerPair.get().getKey();
            } else {
                writer.println(" \n ");
                username = null;
                return true;
            }
            response = reader.readLine();
            if (response.equals("badRegistration")) dialogBoxes.badRegistrationAlert(); // If registration failed
        }
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
                writer.println(loginPair.get().getKey() + "\n" + loginPair.get().getValue()); // Send login info to server
                username = loginPair.get().getKey();
            } else { // If user cancelled
                writer.println(" \n ");
                username = null;
                return true; // Return to askRegister dialog
            }
            response = reader.readLine();
            if (response.equals("badLogin")) dialogBoxes.badLoginAlert(); // If login failed
        }
        return false;
    }

    public static void main(String[] args) {
        launch();
    }
}