package org.beacon.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

    // Appearance tab
    GridPane fontSelectionPane = new GridPane();

    Stage stage = new Stage();
    Scene scene;

    String fontName;
    int fontSize;


    public SettingsWindow(Client client, ChatWindow chatWindow) {
        this.client = client;
        this.chatWindow = chatWindow;
    }

    public void createStage() {
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
}
