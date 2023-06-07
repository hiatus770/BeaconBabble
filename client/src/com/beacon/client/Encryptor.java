package com.beacon.client;

public class Encryptor {
    // Key variables
    private String key; // Password as key

    private boolean readError = false;

    /**
     * Constructor for the encryptor class.
     * @author Jerry
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
        if (this.readError) {
            return "";
        }
        
        return XOR(text, this.key);
    }

    /**
     * Final decryption of the message.
     * @param encryptedText
     * @return decrypted message
     */
    public String decrypt (String encryptedText) {
        if (this.readError) {
            return "";
        }
        
        return XOR(encryptedText, this.key);
    }

    /**
     * Get an ASCII value from 8 digit binary
     * @param binary
     * @return ASCII value
     * @author Jerry 
     */
    private int toAscii (String binary) {
        int output = 0;

        // loop through each location and add to total
        for (int i = 0; i < 8; i++) {
            if (binary.charAt(i) == '1') {
                output += Math.pow(2, 7 - i);
            }
        }

        return output;
    }

    /**
     * Convert binary value to character.
     * @param binary
     * @return character
     * @author Jerry
     */
    private char binaryToChar (String binary) {
        int ascii = toAscii(binary);

        return (char) ascii;
    }

    // apply XOR operation to 2 characters
    /**
     * Apply XOR operation to 2 characters.
     * @param char1
     * @param char2
     * @return XOR result of 2 characters
     * @author Jerry
     */
    private char charXOR (char char1, char char2) {
        if (char1 > 255) { // TODO comment
            return char1;
        }
        // get binary string of each character
        String letter = Integer.toBinaryString(char1);
        String keyLetter = Integer.toBinaryString(char2);

        String output = "";

        // fill in 0s before letter until 8 digits
        int letterLength = 8 - letter.length();

        for (int i = 0; i < letterLength; i++) {
            letter = "0" + letter;
        }

        // fill in 0s before key letter until 8 digits
        int keyCharLength = 8 - keyLetter.length();

        for (int i = 0; i < keyCharLength; i++) {
            keyLetter = "0" + keyLetter;
        }

        // compare each digits of binary of the two characters
        for (int i = 0; i < 8; i++) {
            if (letter.charAt(i) == keyLetter.charAt(i)) { // same, false
                output += "0";
            } else { // different, true
                output += "1";
            }
        }

        // return character
        return binaryToChar(output);
    }

    /**
     * Apply XOR operation to 2 Strings.
     * @param text
     * @param key
     * @return XOR result of 2 Strings
     * @see charXOR for more information on XOR operation on 2 characters
     * @author Jerry
     */
    public String XOR (String text, String key) {
        // get array of characters in text
        char[] characters = text.toCharArray();

        // get array of characters in key + location of key to compare
        int keyLength = key.length();
        char[] keyChar = key.toCharArray();
        int keyPos = 0;

        // encrypted message
        char encryptedChar;
        String encryptedMsg = "";

        // loop through each character within text
        for(char character : characters) {

            // check if reached the end of key
            if (keyPos == keyLength) {
                keyPos -= keyLength;
            }

            // apply XOR to current character
            encryptedChar = charXOR(character, keyChar[keyPos]);

            // next position of key to comare
            keyPos++;

            // update encrypted message
            encryptedMsg += encryptedChar;
        }
        return encryptedMsg;
    }
}
