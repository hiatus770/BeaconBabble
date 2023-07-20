package com.beacon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GUI implements ActionListener {
    JFrame frame;
    JPanel panel;
    JTextArea console;
    JTextField input;

    /**
     * Constructor for the server GUI.
     * @param server the server object, used for a singular boolean 💀
     */
    public GUI(Server server) {
        frame = new JFrame("Beacon");
        panel = new JPanel(new GridBagLayout());
        createLayout();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                server.isRunning = false;
                System.exit(0);
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
        switch (command) {
            case "/help" -> {
                console.append("\n/help - Displays this help message.");
                console.append("\n/exit - Exits the server.");
            }
            case "/exit" -> System.exit(0);
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
            console.append(String.format("\n%s", input.getText()));
            input.setText("");
        }
    }
}
