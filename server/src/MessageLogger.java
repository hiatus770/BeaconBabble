import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.File;

/**
 * This class handles all the information logging for each message and when a user joins with a complete timestamp
 * @author Jamin, Goose, Hiatus770
 */
public class MessageLogger {

    public File logFile;
    /**
     * This method is ran when the server is started and makes sure there is a log file to write to.
     * The method also checks if a file already exists before creating a new file.
     * @author Hiatus770, Jamin, goose
     * @throws IOException if there is an error with creating the file
     */
    public MessageLogger() {
        // Checks if the log file exists, if not, it creates it
        this.logFile = new File("src/resources/logs.txt");
        if (this.logFile.exists()) {
            System.out.println("Log file exists");
        } else {
            try {
                this.logFile.createNewFile();
            } catch (IOException e) {
                System.out.println("An error occurred when creating the log file");
                e.printStackTrace();
            }
        }
    }

    /**
     * This method writes to a log file with the message, user, and time
     * @param message
     * @param user
     * @param time
     * @author Hiatus770, Jamin
     * By the way this method is never used
     */
    public void log(String message, String user, String time) {
        try {
            String messages[] = new String[3];
            messages[1] = message;
            messages[2] = user;
            messages[3] = time;

            // Make sure to append to the file  instead of overwriting it
            FileWriter fileWriter = new FileWriter(this.logFile, true);

            fileWriter.append("\n" + messages[0] + " " + messages[1] + " " + messages[2]);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * This method writes to a log file with the message only and no other information other than just a string 
     * @param message
     */
    public void log(String message){
        try {
            FileWriter fileWriter = new FileWriter(this.logFile, true);
            fileWriter.append("\n").append(message); // intellij says string concatenation is bad and to change to chained append calls
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred when writing to " + logFile);
            e.printStackTrace();
        }
    }
}