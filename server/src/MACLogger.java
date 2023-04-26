import java.io.*;

public class MACLogger {
    File macFile;
    FileWriter fileWriter;

    // create constructor
    public MACLogger() throws IOException {
        // create a new file
        // if the file doesn't exist, create it
        if (!macFile.exists()) {
            macFile = new File("resources/macAddresses.txt");
        }
        FileWriter fileWriter = new FileWriter(macFile, true);
    }

    /**
     * Writes a mac address to the macAddresses.txt file
     * @param macAddress the mac address to be written to the file
     * @throws IOException if there is an error with writing to the file
     */
    public void log(String macAddress) throws IOException {
        fileWriter.write(macAddress);
    }
}