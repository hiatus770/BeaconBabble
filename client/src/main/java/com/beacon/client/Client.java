package com.beacon.client;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
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

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Hello");
        String[] connectionInfo;
        dialogBoxes = new DialogBoxes(this);
        properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/client.properties"));
        run("localhost", 8000);
        /*do connectionInfo = dialogBoxes.connect();
        while (run(connectionInfo[0], Integer.parseInt(connectionInfo[1])) != 0);*/
    }

    public int run(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Account login
            /*
            TODO:
                1. yep
             */


            if (dialogBoxes.askRegister()) {
                writer.println("register"); // Send register request to server
                String[] registerInfo = dialogBoxes.register();
                writer.println(registerInfo[0] + "\n" + registerInfo[1]); // Send register info to server
                username = registerInfo[0];
            } else {
                writer.println("login"); // Send login request to server
                String[] loginInfo = dialogBoxes.login();
                writer.println(loginInfo[0] + "\n" + loginInfo[1]); // Send login info to server
                username = loginInfo[0];
                while (reader.readLine().equals("badLogin")) {
                    dialogBoxes.badLoginAlert();
                    loginInfo = dialogBoxes.login();
                    writer.println(loginInfo[0] + "\n" + loginInfo[1]); // Send login info to server
                    username = loginInfo[0];
                }
            }

            ChatWindow chatWindow = new ChatWindow(this);
            ReadThread readThread = new ReadThread(this, chatWindow);
            chatWindow.appendServerMessage("Connected to server at " + hostname + " on port " + port + ".");

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + hostname);
            dialogBoxes.serverConnectionAlert(hostname, port);
            return 1;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + hostname);
            dialogBoxes.serverConnectionAlert(hostname, port);
            return 2;
        }
        return 0;
    }

    /**
     * Sets the username of the client.
     * @param username the new username of the client.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the client.
     * @return the username of the client.
     */
    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public static void main(String[] args) {
        launch();
    }
}