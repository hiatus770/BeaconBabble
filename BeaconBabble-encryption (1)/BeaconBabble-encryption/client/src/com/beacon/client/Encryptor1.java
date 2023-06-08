package com.beacon.client;


public class Encryptor1 {
    // Key variables
    private String key; // Password as key

    /**
     * Constructor for the encryptor class.
     * @author Jerry Wu
     */
    public Encryptor1 (String key) {
        this.key = key;
    }

    /**
     * Final encryption of the message.
     * @param text
     * @return encrypted message
     */
    public String encrypt (String text) {
        //if (true) return text;

        // get array of character ascii from text
        int[] characters = new int[text.length()];

        for (int pos = 0; pos < text.length(); pos++) {
            characters[pos] = (int) text.charAt(pos);
        }

        // get array of characters in key + location of key to compare
        int keyLength = this.key.length();
        int[] keyChar = new int[keyLength];
        int keyPos = 0;

        // encrypted message
        int encryptedChar;
        String encryptedMsg = "";

        // loop through each character within text
        for(int character : characters) {

            // check if reached the end of key
            if (keyPos == keyLength) {
                keyPos -= keyLength;
            }

            // apply XOR to current character
            encryptedChar = charXOR(character, keyChar[keyPos]);

            // next position of key to comare
            keyPos++;

            // update encrypted message
            encryptedMsg += (encryptedChar + ",");
        }
        // remove extra comma
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
        if (encryptedText == null || encryptedText.length() == 0) return encryptedText;

        // get array of ascii values of encrypted text
        String[] letterAscii = encryptedText.split(",");

        // get array of characters in key + location of key to compare
        int keyLength = this.key.length();
        int[] keyChar = new int[keyLength];
        int keyPos = 0;

        // decrypted message
        int decryptedChar;
        String decryptedMsg = "";

        // loop through each character within text
        for(String character : letterAscii) {

            // check if reached the end of key
            if (keyPos == keyLength) {
                keyPos -= keyLength;
            }

            // apply XOR to current character
            decryptedChar = charXOR(Integer.parseInt(character), keyChar[keyPos]);

            // next position of key to comare
            keyPos++;

            // update decrypted message
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

        // loop through each location and add to total
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

        // loop through each location and add to total
        for (int i = 0; i < 8; i++) {
            double digitSize = Math.pow(2, 7 - i); // value of current digit

            if (ascii >= digitSize) { // ascii have current digit
                ascii -= digitSize;
                output += "1";
            } else { // ascii dont have current digit
                output += "0";
            }
        }

        return output;
    }

    /**
     * Convert binary value to character.
     * @param binary
     * @return character
     * @author jerrybearwu
     */
    private char binaryToChar (String binary) {
        int ascii = toAscii(binary);

        return (char) ascii;
    }

    // apply XOR operation to 2 characters
    /**
     * Apply XOR operation to 2 characters.
     * @param char1 ascii
     * @param char2 ascii
     * @return XOR result of 2 characters in ascii
     * @author jerrybearwu
     */
    private int charXOR (int char1, int char2) {
        // check if ascii greater than 255
        if (char1 > 255 || char2 > 255) {
            return char1;
        }
        
        // get binary string of each character
        String letter = toBinary(char1);
        String keyLetter = toBinary(char2);

        String output = "";

        // compare each digits of binary of the two characters
        for (int i = 0; i < 8; i++) {
            if (letter.charAt(i) == keyLetter.charAt(i)) { // same, false
                output += "0";
            } else { // different, true
                output += "1";
            }
        }

        // return character
        return toAscii(output);
    }
}
