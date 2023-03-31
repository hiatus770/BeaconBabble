// This file will mainly just log all the stuff into a file 
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;   // Import the FileWriter class

/** 
 * This class is responsible for logging all the messages sent by the server and the client.
 * @author goose and hiatus
 * @version 1.0
//  */
// public class Logger {
//     private String filename; 
//     private FileWriter file;

//     public Logger(String filename) {
//         this.filename = filename;
//         this.file = new FileWriter(filename);
//     }

//     public void log(String message) {
//         // write to the file whatever is received in the message 
//         try {
//             file.write(message);
//         } catch (IOException e) {
//             System.out.println("An error occurred.");
//             e.printStackTrace();
//         }
//     }
// }
