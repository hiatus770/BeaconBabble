package com.beacon.client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatWindow implements EventHandler<ActionEvent> {
    Client client;
    BorderPane borderPane;
    GridPane gridPane;
    MenuBar menuBar;

    TextArea messageBox;
    TextField messageInput;
    Button sendButton;

    public ChatWindow(Client client) {
        this.client = client;
        createStage(createRootScene());
    }

    /**
     * Creates the main stage for the chat window.
     * @param scene the scene to be displayed
     */
    public void createStage(Scene scene) {
        Stage stage = new Stage();
        stage.setScene(scene);
        // TODO: implementation
        stage.setOnCloseRequest(event -> {
            System.out.println("closing command");
            System.exit(0);
        });
        stage.setResizable(true);
        // TODO: implement server name into the title ie: "Beacon - Server Name"
        stage.setTitle("Beacon Chat");
        stage.show();
    }

    public Scene createRootScene() {
        borderPane = new BorderPane();
        borderPane.maxHeight(Double.MAX_VALUE);
        borderPane.maxWidth(Double.MAX_VALUE);
        borderPane.setTop(createMenuBar());
        borderPane.setCenter(createGridPane());

        return new Scene(borderPane, 600, 400);
    }

    /**
     * Creates the grid pane for the chat window.
     * @return the grid pane
     */
    private GridPane createGridPane() {
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
        messageInput.addEventHandler(ActionEvent.ACTION, this);

        sendButton = new Button("Send");
        sendButton.setMaxHeight(Double.MAX_VALUE);
        sendButton.setMaxWidth(Double.MAX_VALUE);
        sendButton.addEventHandler(ActionEvent.ACTION, this);

        gridPane.add(messageBox, 0, 0, 2, 1);
        gridPane.add(messageInput, 0, 1, 1, 1);
        gridPane.add(sendButton, 1, 1, 1, 1);

        return gridPane;
    }

    /**
     * Creates the menu bar for the chat window.
     * @return the menu bar
     */
    private MenuBar createMenuBar() {
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

    @Override
    public void handle(ActionEvent event) {
        String message = messageInput.getText().trim();
        if (message.isBlank()) {
            return;
        }
        String timestamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());

        // Shorten message to 2000 characters if it is longer than 2000 characters
        if (message.length() > 2000) {
            message = message.substring(0, 2000);
        }

        client.sendMessage(message);
        messageBox.appendText("\n[" + timestamp + "] <" + client.username + "> " + message);
        messageInput.clear();
    }
}