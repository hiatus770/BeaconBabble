package com.beacon.client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Objects;
import java.util.Optional;

public class DialogBoxes {
    ImageView networkGraphic, loginGraphic;

    public DialogBoxes() {
        networkGraphic = new ImageView(Objects.requireNonNull(this.getClass().getResource("/network.png")).toString());
        networkGraphic.setFitHeight(50);
        networkGraphic.setFitWidth(50);
        loginGraphic = new ImageView(Objects.requireNonNull(this.getClass().getResource("/user_accounts.png")).toString());
        loginGraphic.setFitHeight(50);
        loginGraphic.setFitWidth(50);
    }
    public String[] connect() {
        String[] connectInfo = new String[2];
        Dialog<ButtonType> connectDialog = new Dialog<>();
        connectDialog.setTitle("Connect to Server");
        connectDialog.setHeaderText("Enter the server's IP address and port number.");
        connectDialog.setGraphic(networkGraphic);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        TextField ipAddress = new TextField();
        ipAddress.setPromptText("IP Address");
        TextField portNumber = new TextField();
        portNumber.setPromptText("Port Number");

        gridPane.add(new Label("IP Address:"), 0, 0);
        gridPane.add(ipAddress, 1, 0);
        gridPane.add(new Label("Port Number"), 0, 1);
        gridPane.add(portNumber, 1, 1);

        connectDialog.getDialogPane().setContent(gridPane);

        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        connectDialog.getDialogPane().getButtonTypes().add(connectButtonType);

        connectDialog.setResultConverter(buttonType -> {
            if (buttonType == connectButtonType) {
                if (ipAddress.getText().isBlank() || portNumber.getText().isBlank()) {
                    connectInfo[0] = "";
                    connectInfo[1] = "0";
                } else {
                    connectInfo[0] = ipAddress.getText();
                    connectInfo[1] = portNumber.getText();
                }
            }
            return null;
        });

        connectDialog.showAndWait();

        return connectInfo;
    }

    public String[] register() {
        String[] registerInfo = new String[2];
        Dialog<Pair<String, String>> registerDialog = new Dialog<>();
        registerDialog.setTitle("Register");
        registerDialog.setHeaderText("Enter a username and password.");
        registerDialog.setGraphic(loginGraphic);

        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        registerDialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        gridPane.add(new Label("Username:"), 0, 0);
        gridPane.add(username, 1, 0);
        gridPane.add(new Label("Password:"), 0, 1);
        gridPane.add(password, 1, 1);

        Node registerButton = registerDialog.getDialogPane().lookupButton(registerButtonType);
        registerButton.setDisable(true);
        // Do not allow the user to register if the username is blank
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            registerButton.setDisable(newValue.trim().isEmpty());
        });

        registerDialog.getDialogPane().setContent(gridPane);
        Platform.runLater(username::requestFocus);

        registerDialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = registerDialog.showAndWait();

        result.ifPresent(credentials -> {
            registerInfo[0] = credentials.getKey();
            registerInfo[1] = credentials.getValue();
        });

        return registerInfo;
    }

    public String[] login() {
        String[] loginInfo = new String[2];
        Dialog<Pair<String, String>> loginDialog = new Dialog<>();
        loginDialog.setTitle("Log In");
        loginDialog.setHeaderText("Enter a username and password.");
        loginDialog.setGraphic(loginGraphic);

        ButtonType loginButtonType = new ButtonType("Log In", ButtonBar.ButtonData.OK_DONE);
        loginDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        gridPane.add(new Label("Username:"), 0, 0);
        gridPane.add(username, 1, 0);
        gridPane.add(new Label("Password:"), 0, 1);
        gridPane.add(password, 1, 1);

        Node loginButton = loginDialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);
        // Do not allow the user to register if the username is blank
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        loginDialog.getDialogPane().setContent(gridPane);
        Platform.runLater(username::requestFocus);

        loginDialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            System.exit(0);
            return null;
        });

        Optional<Pair<String, String>> result = loginDialog.showAndWait();

        result.ifPresent(credentials -> {
            loginInfo[0] = credentials.getKey();
            loginInfo[1] = credentials.getValue();
        });

        return loginInfo;
    }

    /**
     * Asks the user if they want to register an account.
     *
     * @return true if the user wants to register an account, false otherwise.
     */
    public boolean askRegister() {
        Alert registerAlert = new Alert(Alert.AlertType.CONFIRMATION);
        registerAlert.setTitle("Sign in");
        registerAlert.setHeaderText("Would you like to log in or register an new account?");
        registerAlert.setContentText("If you already have an account, click \"Log In\".");

        ButtonType loginButton = new ButtonType("Log In");
        ButtonType registerButton = new ButtonType("Register");

        registerAlert.getButtonTypes().setAll(loginButton, registerButton);

        Optional<ButtonType> result = registerAlert.showAndWait();

        // Checks if the result is present and if the result is not the login button
        return result.filter(buttonType -> buttonType != loginButton).isPresent();
    }

    /**
     * Displays an alert to the user if the server could not be connected to.
     */
    public void serverConnectionAlert(String hostname, int port) {
        System.err.println("Couldn't get I/O for the connection to: " + hostname);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Could not connect to the server specified.");
        alert.setContentText("Please enter a valid IP address and port number.\nProvided hostname: " + hostname + "\nProvided port number: " + port);
        alert.showAndWait();
    }

    public void badLoginAlert() {
        System.err.println("Invalid username or password.");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid username or password.");
        alert.setContentText("Please try again.");
        alert.showAndWait();
    }
}
