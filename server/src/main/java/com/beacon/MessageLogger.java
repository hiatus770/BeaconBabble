/*
 * MessageLogger.java
 * Author: Jamin, Goose, Hiatus770
 * This class handles all the information logging for each message and when a user joins with a complete timestamp
 */
package com.beacon;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.File;

/**
 * This class handles all the information logging for each message and when a user joins with a complete timestamp
 * @author Jamin, Oliver, Hiatus 
 * @version 1.0
 * @since June 8th 
 */
public class MessageLogger {
    // The log file
    public File logFile;

    /**
     * This method is ran when the server is started and makes sure there is a log file to write to.
     * The method also checks if a file already exists before creating a new file.
     * @author Hiatus770, Jamin, goose
     * @throws IOException if there is an error with creating the file
     */
    public MessageLogger() throws IOException {
        // Checks if the log file exists, if not, it creates it
        this.logFile = new File("src/main/resources/logs.txt");
        if (this.logFile.exists()) System.out.println("Log file exists");
        else this.logFile.createNewFile();
    }

    /**
     * This method writes to a log file with the message only and no other information other than just a string 
     * @param message message to be written to the log file
     * @throws IOException if there is an error with writing to the file
     */
    public void log(String message) throws IOException {
        // Create a new file writer and append the message to the file
        // The file writer
        FileWriter fileWriter = new FileWriter(logFile, true);
        fileWriter.append("\n").append(message); // intellij says string concatenation is bad and to change to chained append calls
        fileWriter.close();
    }
}