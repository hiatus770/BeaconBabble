package com.beacon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GUI implements ActionListener {
    JFrame frame;
    JPanel panel;
    JTextArea console;
    JTextField input;
    Server server;

    /**
     * Constructor for the server GUI.
     * @param server the server object, used for a singular boolean 💀
     */
    public GUI(Server server) {
        this.server = server;
        frame = new JFrame("Beacon");
        panel = new JPanel(new GridBagLayout());
        createLayout();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                server.isRunning = false;
            }
        });

        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.getContentPane().add(panel);
        console.append("GUI initialized.");
    }

    /**
     * Creates the layout for the server console.
     */
    public void createLayout() {
        console = new JTextArea();
        input = new JTextField();

        console.setEditable(false);
        input.addActionListener(this);

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        panel.add(input, constraints);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridy = 0;
        panel.add(console, constraints);
    }

    public void runCommand (String command) {
        String commandPrefix = command.split(" ")[0];
        switch (commandPrefix) {
            case "/help" -> {
                console.append("\n/help - Displays this help message.");
                console.append("\n/users - Displays the current users.");
                console.append("\n/send <message> - Broadcasts a message.");
                console.append("\n/stop - Stops the server.");
            }
            case "/users" -> {
                console.append(String.format("\nUsers: %s", server.getUsernames()));
            }
            case "/send" -> {
                console.append(String.format("\nMessage sent: %s", command.replace("/send ", "")));
                server.broadcast(command.replace("/send ", ""), null);
            }
            case "/stop" -> server.isRunning = false;
            default -> console.append(String.format("\nUnknown command: %s", command));
        }
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (input.getText().startsWith("/")) {
            console.append(String.format("\n> %s", input.getText()));
            // handle commands
            runCommand(input.getText());
            input.setText("");
        } else {
            console.append(String.format("\n> %s", input.getText()));
            input.setText("");
        }
    }
}
