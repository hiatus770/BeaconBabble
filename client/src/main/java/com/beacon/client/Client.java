package com.beacon.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;


import java.net.*;
import java.io.*;
import java.text.Normalizer;
import java.util.*;

public class Client extends Application {

    Socket socket;

    public static void main(String[] args) {
        connect();
        launch();
    }

    public static void connect() {


        Stage stage = new Stage();
        VBox vBox = new VBox();
        Label hostnameLabel = new Label("Hostname: ");
        TextField hostnameInput = new TextField();
        Label portLabel = new Label("Port: ");
        TextField portInput = new TextField();

        vBox.getChildren().addAll(hostnameLabel, hostnameInput, portLabel, portInput);

        stage.setTitle("Connect to a server...");
        stage.setScene(new Scene(vBox, 400, 300));
    }

    public void sendMessage() {

    }

    @Override
    public void start(Stage stage) throws Exception {
        connect();
    }
}
