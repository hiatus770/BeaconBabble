package com.beacon.client;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Application {
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    DialogBoxes dialogBoxes;

    String username;

    @Override
    public void start(Stage stage) {
        System.out.println("Hello");
        String[] connectionInfo;
        dialogBoxes = new DialogBoxes();
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
                writer.println(registerInfo[0] + " " + registerInfo[1]); // Send register info to server
            } else {
                writer.println("login"); // Send login request to server
                String[] loginInfo = dialogBoxes.login();
                writer.println(loginInfo[0] + " " + loginInfo[1]); // Send login info to server
            }



            ChatWindow chatWindow = new ChatWindow(this);

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + hostname);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not connect to the server specified.");
            alert.setContentText("Please enter a valid IP address and port number.");
            alert.showAndWait();
            return 1;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + hostname);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not connect to the server specified.");
            alert.setContentText("Please enter a valid IP address and port number.");
            alert.showAndWait();
            return 2;
        }
        return 0;
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public static void main(String[] args) {
        launch();
    }
}