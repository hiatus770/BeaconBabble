package com.beacon.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

// TODO: Relocate the inheritance of Application to another class that will be the main class

public class GUI {
    Client client;
    BorderPane borderPane;
    GridPane gridPane;
    MenuBar menuBar;

    TextArea messageBox;
    TextField messageInput;
    Button sendButton;

    public GUI(Client client) {
        createStage(createScene());
    }

    /**
     * Creates a stage for the chat window
     */
    public void createStage(Scene scene) {
        Stage stage = new Stage();
        stage.setTitle("Beacon");
        stage.setScene(scene);
        // akin to the window listener in swing
        stage.setOnCloseRequest(e -> {
            System.out.println("Closing");
            System.exit(0);
        });

        stage.setResizable(true);
        stage.show();
    }

    /**
     * Creates a scene from the chat-view.fxml file
     */
    public Scene createScene() {
        borderPane = new BorderPane();
        borderPane.maxHeight(Double.MAX_VALUE);
        borderPane.maxWidth(Double.MAX_VALUE);
        borderPane.setTop(createMenuBar());
        borderPane.setCenter(createGridPane());

        return new Scene(borderPane, 640, 480);
    }

    public GridPane createGridPane() {
        gridPane = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();

        column1.setHgrow(Priority.ALWAYS);
        column1.setMinWidth(10);
        //column1.setPrefWidth(100);
        column2.setHgrow(Priority.NEVER);
        column2.setMinWidth(10);
        column2.setPrefWidth(75);

        row1.setVgrow(Priority.ALWAYS);
        row1.setMinHeight(10);
        row1.setPrefHeight(30);
        row2.setVgrow(Priority.NEVER);
        row2.setMinHeight(10);
        row2.setPrefHeight(30);

        gridPane.setPadding(new javafx.geometry.Insets(5, 5, 5, 5));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.getColumnConstraints().addAll(column1, column2);
        gridPane.getRowConstraints().addAll(row1, row2);

        messageBox = new TextArea();
        messageBox.setWrapText(true);
        messageBox.setEditable(false);
        messageBox.setMaxHeight(Double.MAX_VALUE);
        messageBox.setMaxWidth(Double.MAX_VALUE);

        messageInput = new TextField();
        messageInput.setPromptText("Enter message here...");
        messageInput.setMaxHeight(Double.MAX_VALUE);
        messageInput.setMaxWidth(Double.MAX_VALUE);
        messageInput.addEventHandler(ActionEvent.ACTION, e -> client.sendMessage());

        sendButton = new Button("Send");
        sendButton.setMaxHeight(Double.MAX_VALUE);
        sendButton.setMaxWidth(Double.MAX_VALUE);

        gridPane.add(messageBox, 0, 0, 2, 1);
        gridPane.add(messageInput, 0, 1, 1, 1);
        gridPane.add(sendButton, 1, 1, 1, 1);

        return gridPane;
    }

    public MenuBar createMenuBar() {
        menuBar = new MenuBar();

        Menu menuFile = new Menu("File");
        MenuItem menuItemSettings = new MenuItem("Settings");
        MenuItem menuItemExit = new MenuItem("Exit");

        menuItemSettings.addEventHandler(ActionEvent.ACTION, e -> {
            System.out.println("Settings button pressed");
            // TODO: create settings window
        });

        menuItemExit.addEventHandler(ActionEvent.ACTION, e -> {
            System.out.println("Exit button pressed, closing");
            System.exit(0);
        });

        menuFile.getItems().addAll(menuItemSettings, menuItemExit);

        menuBar.getMenus().addAll(menuFile);

        return menuBar;
    }
}