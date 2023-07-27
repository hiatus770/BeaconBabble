package org.beacon.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsWindow {
    Client client;
    ChatWindow chatWindow;

    // Panes
    BorderPane borderPane = new BorderPane();
    TabPane tabPane = new TabPane();
    ToolBar toolBar = new ToolBar();

    // Connection tab
    GridPane connectionPane = new GridPane();

    // Appearance tab
    GridPane fontSelectionPane = new GridPane();
    GridPane colorSelectionPane = new GridPane();
    ColorPicker bgColorPicker, frameColorPicker, serverColorPicker, userColorPicker, clientColorPicker;

    Stage stage = new Stage();
    Scene scene;

    String fontName, backgroundColor, frameColor, serverMessageColor, userMessageColor, clientMessageColor;
    int fontSize;


    public SettingsWindow(Client client, ChatWindow chatWindow) {
        this.client = client;
        this.chatWindow = chatWindow;
        stage.setScene(create());
        stage.show();
    }

    public Scene create() {
        Button OKButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        Button applyButton = new Button("Apply");

        OKButton.addEventHandler(ActionEvent.ACTION, event -> {
            chatWindow.preferences.put("font", fontName);
            chatWindow.preferences.putInt("font-size", fontSize);

            /*
            use later
            chatWindow.preferences.put("background-color", backgroundColor);
            chatWindow.preferences.put("frame-color", frameColor);
            chatWindow.preferences.put("server-message-color", serverMessageColor);
            chatWindow.preferences.put("user-message-color", userMessageColor);
            chatWindow.preferences.put("client-message-color", clientMessageColor);*/

            try {
                chatWindow.loadSettings();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.close();
        });

        cancelButton.addEventHandler(ActionEvent.ACTION, event -> stage.close());

        applyButton.addEventHandler(ActionEvent.ACTION, event -> {
            chatWindow.preferences.put("font", fontName);
            chatWindow.preferences.putInt("font-size", fontSize);
            /*chatWindow.preferences.put("background-color", backgroundColor);
            chatWindow.preferences.put("frame-color", frameColor);
            chatWindow.preferences.put("server-message-color", serverMessageColor);
            chatWindow.preferences.put("user-message-color", userMessageColor);
            chatWindow.preferences.put("client-message-color", clientMessageColor);*/

            try {
                chatWindow.loadSettings();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        toolBar.getItems().addAll(OKButton, cancelButton, applyButton);

        borderPane.setCenter(tabPane);
        borderPane.setBottom(toolBar);

        tabPane.getTabs().addAll(createAppearanceTab());

        scene = new Scene(borderPane, 800, 600);
        return scene;
    }

    public Tab createConnectionTab() {
        Tab tab = new Tab("Connection");
        AnchorPane anchorPane = new AnchorPane();
        GridPane connectionPane = new GridPane();

        // Anchor pane settings
        AnchorPane.setBottomAnchor(connectionPane, 0.0);
        AnchorPane.setTopAnchor(connectionPane, 0.0);
        AnchorPane.setLeftAnchor(connectionPane, 0.0);
        AnchorPane.setRightAnchor(connectionPane, 0.0);
        anchorPane.setPadding(new javafx.geometry.Insets(10));

        // Grid pane settings
        connectionPane.setHgap(5);
        connectionPane.setVgap(5);

        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints();
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.NEVER);
            column.setHgrow(Priority.NEVER);
            connectionPane.getColumnConstraints().add(column);
            connectionPane.getRowConstraints().add(row);
        }

        connectionPane.add(new Label("Server address"), 0, 0);
        connectionPane.add(new Label("Server port"), 0, 1);
        connectionPane.add(new Label("Server name"), 0, 2);
        connectionPane.add(new TextField(), 1, 0);
        connectionPane.add(new TextField(), 1, 1);
        connectionPane.add(new TextField(), 1, 2);

        anchorPane.getChildren().add(connectionPane);

        tab.setContent(anchorPane);
        return tab;
    }

    public Tab createAppearanceTab() {
        Tab tab = new Tab("Appearance");
        AnchorPane anchorPane = new AnchorPane();

        // Containers for the font and color selection
        GridPane gridPane = new GridPane();

        // Anchor pane settings
        anchorPane.setPrefHeight(180);
        anchorPane.setPrefWidth(200);

        // Grid pane settings
        gridPane.setHgap(10);
        gridPane.setPadding(new javafx.geometry.Insets(10));

        gridPane.add(createFontPane(), 0, 0);
        //gridPane.add(createColorPane(), 1, 0);

        AnchorPane.setTopAnchor(gridPane, 0.0);
        AnchorPane.setBottomAnchor(gridPane, 0.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);

        anchorPane.getChildren().add(gridPane);

        tab.setContent(anchorPane);
        return tab;
    }

    /**
     * Creates a pane for selecting colors.
     * @return a GridPane containing the color selection pane
     */
    public GridPane createColorPane() {
        colorSelectionPane = new GridPane();

        colorSelectionPane.setHgap(5);
        colorSelectionPane.setVgap(5);

        // add 6 rows with VGrow set to NEVER
        for (int i = 0; i < 6; i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.NEVER);
            colorSelectionPane.getRowConstraints().add(row);
        }

        Label applicationColorLabel = new Label("Application Colours");
        applicationColorLabel.fontProperty().set(Font.font("System", FontWeight.BOLD, 12));

        colorSelectionPane.add(applicationColorLabel, 0, 0);
        colorSelectionPane.add(new Label("Background colour"), 0, 1);
        colorSelectionPane.add(new Label("Frame colour"), 0, 2);
        colorSelectionPane.add(new Label("Server message colour"), 0, 3);
        colorSelectionPane.add(new Label("Your message colour"), 0, 4);
        colorSelectionPane.add(new Label("Other message colour"), 0, 5);

        bgColorPicker = new ColorPicker(Color.WHITE);
        frameColorPicker = new ColorPicker(Color.WHITE);
        serverColorPicker = new ColorPicker(Color.RED);
        userColorPicker = new ColorPicker(Color.GREEN);
        clientColorPicker = new ColorPicker(Color.BLUE);

        backgroundColor = toHexCode(bgColorPicker.getValue());
        frameColor = toHexCode(frameColorPicker.getValue());
        serverMessageColor = toHexCode(serverColorPicker.getValue());
        userMessageColor = toHexCode(userColorPicker.getValue());
        clientMessageColor = toHexCode(clientColorPicker.getValue());

        bgColorPicker.addEventHandler(ActionEvent.ACTION, event -> backgroundColor = toHexCode(bgColorPicker.getValue()));
        frameColorPicker.addEventHandler(ActionEvent.ACTION, event -> frameColor = toHexCode(frameColorPicker.getValue()));
        serverColorPicker.addEventHandler(ActionEvent.ACTION, event -> serverMessageColor = toHexCode(serverColorPicker.getValue()));
        userColorPicker.addEventHandler(ActionEvent.ACTION, event -> userMessageColor = toHexCode(userColorPicker.getValue()));
        clientColorPicker.addEventHandler(ActionEvent.ACTION, event -> clientMessageColor = toHexCode(clientColorPicker.getValue()));

        colorSelectionPane.add(bgColorPicker, 1, 1);
        colorSelectionPane.add(frameColorPicker, 1, 2);
        colorSelectionPane.add(serverColorPicker, 1, 3);
        colorSelectionPane.add(userColorPicker, 1, 4);
        colorSelectionPane.add(clientColorPicker, 1, 5);

        return colorSelectionPane;
    }

    /**
     * Creates a pane for selecting a font.
     * @return a GridPane containing the font selection pane
     */
    public GridPane createFontPane() {
        fontSelectionPane = new GridPane();

        fontSelectionPane.setHgap(5);
        fontSelectionPane.setVgap(5);

        ColumnConstraints fontColumn = new ColumnConstraints();
        ColumnConstraints styleColumn = new ColumnConstraints();
        ColumnConstraints sizeColumn = new ColumnConstraints();
        RowConstraints labelRow = new RowConstraints();
        RowConstraints listRow = new RowConstraints();
        RowConstraints sampleRow = new RowConstraints();

        fontColumn.setHgrow(Priority.SOMETIMES);
        styleColumn.setHgrow(Priority.NEVER);
        sizeColumn.setHgrow(Priority.NEVER);
        labelRow.setVgrow(Priority.NEVER);
        listRow.setVgrow(Priority.ALWAYS);
        sampleRow.setVgrow(Priority.NEVER);

        // Settings column constraints
        fontSelectionPane.getColumnConstraints().addAll(fontColumn, styleColumn, sizeColumn);
        fontSelectionPane.getRowConstraints().addAll(labelRow, listRow, sampleRow);

        // Font selection labels
        Label fontLabel = new Label("Font");
        Label styleLabel = new Label("Weight");
        Label sizeLabel = new Label("Size");
        fontLabel.fontProperty().set(Font.font("System", FontWeight.BOLD, 12));
        styleLabel.fontProperty().set(Font.font("System", FontWeight.BOLD, 12));
        sizeLabel.fontProperty().set(Font.font("System", FontWeight.BOLD, 12));

        // Font selection nodes
        ListView<String> fontListView = new ListView<>();
        ListView<Integer> sizeListView = new ListView<>();

        // List view contents for the font selection
        ObservableList<String> fontList = FXCollections.observableArrayList(Font.getFamilies());
        ObservableList<Integer> sizeList = FXCollections.observableArrayList(8, 9, 10, 11, 12, 14, 16, 18, 24, 30, 36, 48, 60, 72, 96);

        fontListView.setItems(fontList);
        sizeListView.setItems(sizeList);

        fontName = chatWindow.preferences.get("font", "System");
        fontSize = chatWindow.preferences.getInt("font-size", 12);

        // Font sample nodes
        TextFlow textFlow = new TextFlow();
        Text sampleText = new Text("Sample: The quick brown fox jumps over the lazy dog.");

        textFlow.getChildren().add(sampleText);

        fontListView.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            fontName = fontListView.getSelectionModel().getSelectedItem();
            sampleText.fontProperty().set(Font.font(fontName));
        });

        sizeListView.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            fontSize = sizeListView.getSelectionModel().getSelectedItem();
            sampleText.fontProperty().set(Font.font(fontName, fontSize));
        });

        fontSelectionPane.add(fontLabel, 0, 0);
        fontSelectionPane.add(sizeLabel, 1, 0);

        fontSelectionPane.add(fontListView, 0, 1);
        fontSelectionPane.add(sizeListView, 1, 1);

        fontSelectionPane.add(textFlow, 0, 2, 2, 1);

        return fontSelectionPane;
    }

    /**
     * Converts a JavaFX Color object to a hex code.
     * @param color the color to convert
     * @return the hex code of the color
     */
    public String toHexCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
