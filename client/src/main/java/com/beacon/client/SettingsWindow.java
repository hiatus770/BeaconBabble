package com.beacon.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.List;

public class SettingsWindow {

    // Panes
    BorderPane borderPane = new BorderPane();
    TabPane tabPane = new TabPane();
    ToolBar toolBar = new ToolBar();

    // Connection tab
    GridPane connectionPane = new GridPane();

    // Appearance tab
    GridPane fontSelectionPane = new GridPane();
    GridPane colorSelectionPane = new GridPane();

    Stage stage = new Stage();
    Scene scene;

    String fontName, fontSize, fontColor, backgroundColor, frameColor, serverMessageColor, userMessageColor, clientMessageColor;

    public SettingsWindow() {
        stage.setScene(create());
    }

    public Scene create() {
        Button OKButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        Button applyButton = new Button("Apply");

        OKButton.addEventHandler(ActionEvent.ACTION, event -> {
            ListView<String> fontNameSelection = (ListView<String>) fontSelectionPane.getChildren().get(0);
            ListView<String> fontSizeSelection = (ListView<String>) fontSelectionPane.getChildren().get(1);
            ListView<String> fontColorSelection = (ListView<String>) fontSelectionPane.getChildren().get(2);

            fontName = fontNameSelection.getSelectionModel().getSelectedItem();
            fontSize = fontSizeSelection.getSelectionModel().getSelectedItem();
            fontColor = fontColorSelection.getSelectionModel().getSelectedItem();

            backgroundColor = colorSelectionPane.getChildren().get(6).toString();
            frameColor = colorSelectionPane.getChildren().get(7).toString();
            serverMessageColor = colorSelectionPane.getChildren().get(8).toString();
            userMessageColor = colorSelectionPane.getChildren().get(9).toString();
            clientMessageColor = colorSelectionPane.getChildren().get(10).toString();
            System.out.println(fontName + fontSize + fontColor + backgroundColor + frameColor + serverMessageColor + userMessageColor + clientMessageColor);
            stage.close();
        });

        cancelButton.addEventHandler(ActionEvent.ACTION, event -> {
            stage.close();
        });

        applyButton.addEventHandler(ActionEvent.ACTION, event -> {
            System.out.println("Apply");
        });

        toolBar.getItems().addAll(OKButton, cancelButton, applyButton);

        borderPane.setCenter(tabPane);
        borderPane.setBottom(toolBar);

        tabPane.getTabs().addAll(createConnectionTab(), createAppearanceTab());

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
        gridPane.add(createColorPane(), 0, 1);

        AnchorPane.setTopAnchor(gridPane, 0.0);
        AnchorPane.setBottomAnchor(gridPane, 0.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);

        tab.setContent(anchorPane);
        return tab;
    }

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

        colorSelectionPane.add(new ColorPicker(Color.WHITE), 1, 1);
        colorSelectionPane.add(new ColorPicker(Color.WHITE), 1, 2);
        colorSelectionPane.add(new ColorPicker(Color.RED), 1, 3);
        colorSelectionPane.add(new ColorPicker(Color.GREEN), 1, 4);
        colorSelectionPane.add(new ColorPicker(Color.BLUE), 1, 5);

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
        fontLabel.fontProperty().set(Font.font("System", FontWeight.BOLD, 12));
        Label styleLabel = new Label("Style");
        styleLabel.fontProperty().set(Font.font("System", FontWeight.BOLD, 12));
        Label sizeLabel = new Label("Size");
        sizeLabel.fontProperty().set(Font.font("System", FontWeight.BOLD, 12));

        // Font selection nodes
        ListView<String> fontListView = new ListView<>();
        ListView<String> styleListView = new ListView<>();
        ListView<Integer> sizeListView = new ListView<>();

        // List view contents for the font selection
        ObservableList<String> fontList = FXCollections.observableArrayList(Font.getFamilies());
        ObservableList<String> styleList = FXCollections.observableArrayList(); // Depends on the font selected
        ObservableList<Integer> sizeList = FXCollections.observableArrayList(8, 9, 10, 11, 12, 14, 16, 18, 24, 30, 36, 48, 60, 72, 96);

        fontListView.setItems(fontList);
        styleListView.setItems(styleList);
        sizeListView.setItems(sizeList);

        // Font sample nodes
        TextFlow textFlow = new TextFlow();
        Text sampleText = new Text("Sample: The quick brown fox jumps over the lazy dog.");

        textFlow.getChildren().add(sampleText);

        fontListView.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            String font = fontListView.getSelectionModel().getSelectedItem();
            styleList.clear();
            styleList.addAll(Font.getFontNames(font));
            styleListView.setItems(styleList);
            sampleText.fontProperty().set(Font.font(font));
        });

        styleListView.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            String style = styleListView.getSelectionModel().getSelectedItem();
            sampleText.fontProperty().set(Font.font(style));
        });

        sizeListView.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            int size = sizeListView.getSelectionModel().getSelectedItem();
            sampleText.fontProperty().set(Font.font(size));
        });


        fontSelectionPane.add(fontLabel, 0, 0);
        fontSelectionPane.add(styleLabel, 1, 0);
        fontSelectionPane.add(sizeLabel, 2, 0);

        fontSelectionPane.add(fontListView, 0, 1);
        fontSelectionPane.add(styleListView, 1, 1);
        fontSelectionPane.add(sizeListView, 2, 1);

        fontSelectionPane.add(textFlow, 0, 2, 3, 1);

        return fontSelectionPane;
    }
}
