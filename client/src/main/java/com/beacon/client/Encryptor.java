/*
 * Encryptor.java
 * Author: Jerry Wu
 * Encrypts and decrypts messages using XOR encryption.
 */
package com.beacon.client;

public class Encryptor {
    // Key variables
    private String key; // Password as key

    /**
     * Constructor for the encryptor class.
     * @author Jerry Wu
     */
    public Encryptor (String key) {
        this.key = key;
    }

    /**
     * Final encryption of the message.
     * @param text
     * @return encrypted message
     */
    public String encrypt (String text) {
        // Get array of character ascii from text
        int[] characters = new int[text.length()];

        for (int pos = 0; pos < text.length(); pos++) {
            characters[pos] = (int) text.charAt(pos);
        }

        // Get array of characters in key + location of key to compare
        int keyLength = this.key.length();
        int[] keyChar = new int[keyLength];
        int keyPos = 0;

        for (int pos = 0; pos < keyLength; pos++) {
            keyChar[pos] = (int) this.key.charAt(pos);
        }

        // Encrypted message
        int encryptedChar;
        String encryptedMsg = "";

        // Loop through each character within text
        for(int character : characters) {

            // Check if reached the end of key
            if (keyPos == keyLength) {
                keyPos -= keyLength;
            }

            // Apply XOR to current character
            encryptedChar = charXOR(character, keyChar[keyPos]);

            // Next position of key to comare
            keyPos++;

            // Update encrypted message
            encryptedMsg += (encryptedChar + ",");
        }
        // Remove extra comma, if not empty message
        if (encryptedMsg.length() > 0) {
            encryptedMsg = encryptedMsg.substring(0, encryptedMsg.length() - 1);
        }

        return encryptedMsg;
    }

    /**
     * Final decryption of the message.
     * @param encryptedText
     * @return decrypted message
     */
    public String decrypt (String encryptedText) {
        // Make sure no empty messaging decrypting
        if (encryptedText == null || encryptedText.length() == 0) return encryptedText;

        // Get array of ascii values of encrypted text
        String[] letterAscii = encryptedText.split(",");

        // Get array of characters in key + location of key to compare
        int keyLength = this.key.length();
        int[] keyChar = new int[keyLength];
        int keyPos = 0;

        for (int pos = 0; pos < keyLength; pos++) {
            keyChar[pos] = (int) this.key.charAt(pos);
        }

        // Decrypted message
        int decryptedChar;
        String decryptedMsg = "";

        // Loop through each character within text
        for(String character : letterAscii) {

            // Check if reached the end of key
            if (keyPos == keyLength) {
                keyPos -= keyLength;
            }

            // Apply XOR to current character
            decryptedChar = charXOR(Integer.parseInt(character), keyChar[keyPos]);

            // Next position of key to comare
            keyPos++;

            // Update decrypted message
            decryptedMsg += (char) decryptedChar;
        }

        return decryptedMsg;
    }

    /**
     * Get an ASCII value from 8 digit binary
     * @param binary
     * @return ASCII value
     * @author jerrybearwu 
     */
    private int toAscii (String binary) {
        int output = 0;

        // Loop through each location and add to total
        for (int i = 0; i < 8; i++) {
            if (binary.charAt(i) == '1') {
                output += Math.pow(2, 7 - i);
            }
        }

        return output;
    }

    /**
     * Get an binary String value from interger ASCII
     * @param ascii
     * @return Binary String
     * @author jerrybearwu 
     */
    private String toBinary (int ascii) {
        String output = "";

        // Loop through each location and add to total
        for (int i = 0; i < 8; i++) {
            double digitSize = Math.pow(2, 7 - i); // Value of current digit

            if (ascii >= digitSize) { // ASCII have current digit
                ascii -= digitSize;
                output += "1";
            } else { // ASCII dont have current digit
                output += "0";
            }
        }

        return output;
    }

    /**
     * Apply XOR operation to 2 characters.
     * @param char1 ASCII
     * @param char2 ASCII
     * @return XOR result of 2 characters in ascii
     * @author jerrybearwu
     */
    private int charXOR (int char1, int char2) {
        // Check if ASCII greater than 255
        if (char1 > 255 || char2 > 255) {
            return char1;
        }
        
        // Get binary string of each character
        String letter = toBinary(char1);
        String keyLetter = toBinary(char2);

        String output = "";

        // Compare each digits of binary of the two characters
        for (int i = 0; i < 8; i++) {
            if (letter.charAt(i) == keyLetter.charAt(i)) { // Same, false
                output += "0";
            } else { // Different, true
                output += "1";
            }
        }

        // Return character
        return toAscii(output);
    }
}
