package org.beacon.client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DialogBoxes {
    ImageView networkGraphic, loginGraphic;
    Client client;

    /**
     * Constructor for the DialogBoxes class.
     * Creates icons for the dialog boxes.
     * @param client the client object, used for a few methods
     */
    public DialogBoxes(Client client) {
        networkGraphic = new ImageView(Objects.requireNonNull(this.getClass().getResource("/icons/network.png")).toString());
        networkGraphic.setFitHeight(50);
        networkGraphic.setFitWidth(50);

        loginGraphic = new ImageView(Objects.requireNonNull(this.getClass().getResource("/icons/user_accounts.png")).toString());
        loginGraphic.setFitHeight(50);
        loginGraphic.setFitWidth(50);

        this.client = client;
    }

    /**
     * Creates a dialog box for connecting to a server.
     * @return an array of strings containing the IP address and port number
     */
    public Optional<Pair<String, Integer>> connectDialog() {
        Dialog<Pair<String, Integer>> connectDialog = new Dialog<>();
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
        gridPane.add(new Label("Port Number:"), 0, 1);
        gridPane.add(portNumber, 1, 1);

        connectDialog.getDialogPane().setContent(gridPane);

        portNumber.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            if (!"0123456789".contains(event.getCharacter()) || portNumber.getText().length() > 4) {
                event.consume();
            }
        });

        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        connectDialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        connectDialog.setResultConverter(buttonType -> {
            if (buttonType == connectButtonType) {
                if (ipAddress.getText().isBlank() || portNumber.getText().isBlank()) {
                    return new Pair<>("", 0);
                } else {
                    return new Pair<>(ipAddress.getText(), Integer.parseInt(portNumber.getText()));
                }
            }
            return null;
        });

        return connectDialog.showAndWait();
    }

    /**
     * Creates a dialog box for registering a new user.
     * @return an array of strings containing the username and password
     */
    public Optional<Pair<String, String>> register() {
        Dialog<Pair<String, String>> registerDialog = new Dialog<>();
        registerDialog.setTitle("Register");
        registerDialog.setHeaderText("Enter a username and password.");
        registerDialog.setContentText("Username may not include the following characters: , : ; | \\ / * ? \" < >");
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
            Pattern pattern = Pattern.compile("[^A-Za-z0-9._-]"); // Regex for invalid characters
            Matcher matcher = pattern.matcher(newValue);

            if (matcher.find()) {
                username.setText(oldValue);
                registerButton.setDisable(matcher.find());
            }

            registerButton.setDisable(newValue.trim().isEmpty());
        });

        registerDialog.getDialogPane().setContent(gridPane);
        Platform.runLater(username::requestFocus);

        registerDialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) return new Pair<>(username.getText(), password.getText());
            return null;
        });

        return registerDialog.showAndWait();
    }

    /**
     * Creates a dialog box for logging in.
     * @return an array of strings containing the username and password
     */
    public Optional<Pair<String, String>> login() {
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
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        gridPane.add(new Label("Username:"), 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(new Label("Password:"), 0, 1);
        gridPane.add(passwordField, 1, 1);

        Node loginButton = loginDialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);
        // Do not allow the user to register if the username is blank
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

        loginDialog.getDialogPane().setContent(gridPane);
        Platform.runLater(usernameField::requestFocus);

        loginDialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) return new Pair<>(usernameField.getText(), passwordField.getText());
            return null;
        });

        return loginDialog.showAndWait();
    }

    /**
     * Asks the user if they want to register an account.
     * @return 0 if the user wants to log in, 1 if the user wants to register.
     */
    public int askRegister() {
        Dialog<Integer> registerDialog = new Dialog<>();
        registerDialog.setTitle("Sign in");
        registerDialog.setHeaderText("Would you like to log in or register an new account?");
        registerDialog.setContentText("If you already have an account for this server, click \"Log In\".");

        ButtonType loginButton = new ButtonType("Log In", ButtonBar.ButtonData.OK_DONE);
        ButtonType registerButton = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);

        registerDialog.getDialogPane().getButtonTypes().setAll(loginButton, registerButton, ButtonType.CANCEL);

        registerDialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButton) return 0;
            else if (dialogButton == registerButton) return 1;
            return 2;
        });

        Optional<Integer> result = registerDialog.showAndWait();
        return result.orElse(2);
    }

    /**
     * Displays an alert to the user if the server could not be connected to.
     */
    public void unknownHostAlert(String hostname, int port) {
        System.err.println("Unknown host: " + hostname);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Could not connect to the server specified.");
        alert.setContentText("Please enter a valid IP address and port number.\nError: Unknown Host\nProvided hostname: " + hostname + "\nProvided port number: " + port);
        alert.showAndWait();
    }

    /**
     * Displays an alert to the user if the server could not be connected to.
     */
    public void IOConnectionAlert(String hostname, int port) {
        System.err.println("Couldn't get I/O for the connection to: " + hostname);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Could not connect to the server specified.");
        alert.setContentText("Please enter a valid IP address and port number.\nError: Couldn't get I/O for the connection\nProvided hostname: " + hostname + "\nProvided port number: " + port);
        alert.showAndWait();
    }

    /**
     * Displays an alert to the user if the username or password is invalid.
     */
    public void badLoginAlert() {
        System.err.println("Invalid username or password.");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid username or password.");
        alert.setContentText("Please try again.");
        alert.showAndWait();
    }

    /**
     * Displays an alert to the user if the username is already taken.
     */
    public void badRegistrationAlert() {
        System.err.println("Username already taken.");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Username already taken.");
        alert.setContentText("Please try again.");
        alert.showAndWait();
    }

    /**
     * Displays an alert to the user if the port number is invalid (over 65535).
     */
    public void invalidPortAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid port number.");
        alert.setContentText("Please enter a valid port number that is between 0 and 65535.");
        alert.showAndWait();
    }
}
