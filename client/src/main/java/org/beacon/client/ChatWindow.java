package org.beacon.client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.Caret;
import org.fxmisc.richtext.CaretNode;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.prefs.Preferences;

public class ChatWindow implements EventHandler<ActionEvent> {
    Stage stage;
    Scene scene;
    Client client;
    BorderPane borderPane;
    GridPane gridPane;
    MenuBar menuBar;

    TextField messageInput;
    Button sendButton;
    StyleClassedTextArea chatBox;
    CaretNode caret;
    VirtualizedScrollPane<StyleClassedTextArea> chatBoxScrollPane;

    Preferences preferences = Preferences.userRoot().node(SettingsWindow.class.getName());

    /**
     * ChatWindow constructor.
     * @param client the client object
     * @throws IOException if the style sheet cannot be found
     */
    public ChatWindow(Client client) throws IOException {
        this.client = client;
        createStage(createRootScene());
    }

    /**
     * Creates the main stage for the chat window.
     * @param scene the scene to be displayed
     */
    public void createStage(Scene scene) {
        stage = new Stage();
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            client.exit();
            stage.close();
            System.exit(0);
        });

        stage.setResizable(true);
        // TODO: implement server name into the title ie: "Beacon - Server Name"
        stage.setTitle("Beacon");
        stage.show();
    }

    public Scene createRootScene() throws IOException {
        borderPane = new BorderPane();
        borderPane.maxHeight(Double.MAX_VALUE);
        borderPane.maxWidth(Double.MAX_VALUE);
        borderPane.setTop(createMenuBar());
        borderPane.setCenter(createGridPane());
        scene = new Scene(borderPane, 600, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

        loadSettings();

        return scene;
    }

    /**
     * Creates the grid pane for the chat window.
     * @return the grid pane
     * @throws IOException if the style sheet cannot be found
     */
    private GridPane createGridPane() throws IOException {
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

        chatBox = new StyleClassedTextArea();
        chatBox.setEditable(false);
        chatBox.setWrapText(true);
        chatBox.setMaxHeight(Double.MAX_VALUE);
        chatBox.setMaxWidth(Double.MAX_VALUE);
        chatBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        caret = new CaretNode("caret", chatBox);
        caret.setShowCaret(Caret.CaretVisibility.OFF);
        chatBox.addCaret(caret);
        chatBoxScrollPane = new VirtualizedScrollPane<>(chatBox);

        messageInput = new TextField();
        messageInput.setPromptText("Enter message here...");
        messageInput.setMaxHeight(Double.MAX_VALUE);
        messageInput.setMaxWidth(Double.MAX_VALUE);
        messageInput.addEventHandler(ActionEvent.ACTION, this);

        sendButton = new Button("Send");
        sendButton.setMaxHeight(Double.MAX_VALUE);
        sendButton.setMaxWidth(Double.MAX_VALUE);
        sendButton.addEventHandler(ActionEvent.ACTION, this);

        gridPane.add(chatBoxScrollPane, 0, 0, 2, 1);
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
        MenuItem menuItemSave = new MenuItem("Save Chat Log");
        MenuItem menuItemSettings = new MenuItem("Settings");
        MenuItem menuItemExit = new MenuItem("Exit");

        menuItemSave.addEventHandler(ActionEvent.ACTION, e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Save Chat Log");
            File chatLog = directoryChooser.showDialog(stage);
            try (FileWriter fileWriter = new FileWriter(chatLog + "/chatlog.txt")) {
                fileWriter.append(chatBox.getText());
                chatBox.append("Chat log saved to " + chatLog.getAbsolutePath().replaceAll("\\\\", "/") + "/chatlog.txt\n", "server");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        menuItemSettings.addEventHandler(ActionEvent.ACTION, e -> {
            SettingsWindow settingsWindow = new SettingsWindow(client, this);
        });

        menuItemExit.addEventHandler(ActionEvent.ACTION, e -> {
            client.exit();
            stage.close();
            System.exit(0);
        });

        menuFile.getItems().addAll(menuItemSave, menuItemSettings, menuItemExit);

        menuBar.getMenus().addAll(menuFile);

        return menuBar;
    }

    /**
     * Loads the settings from the preferences.
     * @throws IOException if the style sheet cannot be found
     */
    public void loadSettings() throws IOException {
        chatBox.setStyle(
                String.format("-fx-font: %dpx \"%s\";", preferences.getInt("font-size", 12), preferences.get("font", "System")) +
                "-fx-padding: 5 5 5 5;" +
                String.format("-fx-background-color: %s;", preferences.get("background-color", "white")) +
                "-fx-border-color: gray;" +
                "-fx-border-style: solid inside line-join round;" +
                "-fx-border-width: 0.5px;" +
                "-fx-border-radius: 3px;"
        );
    }

    /**
     * Appends a red message to the chat box.
     * @param message the message to append
     */
    public void appendServerMessage(String message) {
        chatBox.append(message, "server");
        chatBoxScrollPane.scrollBy(0, 100);
    }

    /**
     * Appends a blue message to the chat box.
     * @param message the message to append
     */
    public void appendUserMessage(String message) {
        chatBox.append(message, "user");
        chatBoxScrollPane.scrollBy(0, 100);
    }

    /**
     * Appends a green message to the chat box.
     * @param message the message to append
     */
    public void appendClientMessage(String message) {
        chatBox.append(message, "client");
        chatBoxScrollPane.scrollBy(0, 100);
    }

    /**
     * Handles the action event for the send button and message input.
     * @param event the action event
     */
    @Override
    public void handle(ActionEvent event) {
        String message = messageInput.getText().trim();
        if (message.isBlank()) {
            return;
        } else if (message.equals("/exit")) {
            client.exit();
            stage.close();
            System.exit(0);
        }
        String timestamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());

        // Shorten message to 2000 characters if it is longer than 2000 characters
        if (message.length() > 2000) {
            message = message.substring(0, 2000);
        }

        client.sendMessage(message);
        appendClientMessage("[" + timestamp + "] <" + client.getUsername() + "> " + message + "\n");
        messageInput.clear();
    }
}