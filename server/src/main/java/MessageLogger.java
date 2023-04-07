import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.File;

/**
 * This class handles all the information logging for each message and when a user joins with a complete timestamp
 * @author Jamin, Goose, Hiatus770
 */
public class MessageLogger {

    String logFile = "resources/logs.txt";
    /**
     * This method is ran when the server is started and makes sure there is a log file to write to, otherwise it will throw an error
     * @param args
     * @author Hiatus770, Jamin
     * @throws IOException
     */
    public static void main(String args[]){
        // Checks if the log file exists, if not, it creates it
        File logFile = new File("resources/logs.txt");
        if (logFile.exists()) {
            System.out.println("Log file exists");
        } else {
            try {
                logFile.createNewFile();
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
     */
    public void log(String message, String user, String time) {
        try {
            String messages[] = new String[3];
            messages[1] = message;
            messages[2] = user;
            messages[3] = time;

            // Make sure to append to the file  instead of overwriting it
            FileWriter myWriter = new FileWriter(logFile, true);

            myWriter.append("\n" + messages[0] + " " + messages[1] + " " + messages[2]);
            myWriter.close();
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
            FileWriter myWriter = new FileWriter(logFile, true);
            myWriter.append("\n" + message);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred when writing to " + logFile);
            e.printStackTrace();
        }
    }
}