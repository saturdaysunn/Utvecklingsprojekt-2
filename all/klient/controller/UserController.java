package all.klient.controller;

import java.io.*;
import java.util.*;

/**
 * takes care of reading and writing to files (contacts, messages, etc)
 */
public class UserController { //TODO: change name to FileController?

    public UserController() {

    }

    public void saveUserToFile(String text, String filePath) {
        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(text);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public LinkedList<String> retrieveAllUsersFromFile(String filePath) {
        LinkedList<String> lines = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public ArrayList<String> getContactsOfUser(String filePath, String searchString) {
        ArrayList<String> resultList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() && found) { // Empty row found after the search string
                    break; // Stop reading after finding the next empty row
                }
                if (found && !line.isEmpty()) { // Add lines after the search string
                    resultList.add(line);
                }
                if (line.equals(searchString)) {
                    found = true; // Set found to true when the search string is found
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException appropriately
        }

        return resultList;
    }

    /**
     * This method removes data blocks from a text file that start with a specific
     * string and are bounded by empty rows before and after.
     *
     * @param filePath The path to the text file.
     * @param targetString The string to search for.
     * @param outputFilePath The path to write the modified text file.
     * @throws IOException If an I/O error occurs while reading or writing the file.
     */
    public void removeDataBlock(String filePath, String targetString, String outputFilePath) throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new FileReader(filePath));
            writer = new BufferedWriter(new FileWriter(outputFilePath));

            String line;
            boolean isInBlock = false; //flag to indicate if current line is within the block to remove

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) { //check for empty row
                    if (isInBlock) {
                        isInBlock = false; //mark end of block
                    }
                } else if (line.contains(targetString)) { //check if line contains target string
                    isInBlock = true; //start of the block
                }

                if (!isInBlock) {
                    //write line to output file only if it's not within the block
                    writer.write(line);
                    writer.newLine();
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
    public void rewriteContactsTextFileWithNewContacts(HashMap<String, ArrayList<String>> hashMap, String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename));
             BufferedWriter writer = new BufferedWriter(new FileWriter("all/files/temp.txt"))) {

            boolean foundKey = false;
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !foundKey) {
                    //ignore lines until an empty line is found before the key
                    continue;
                } else if (line.trim().isEmpty() && !foundKey) {
                    foundKey = true;
                } else if (foundKey && line.trim().isEmpty()) {
                    break;
                }
                writer.write(line);
                writer.newLine();
            }

            writer.newLine(); //add an empty line before writing the HashMap
            writeHashMapToFile(hashMap, writer);

            //copy the rest of the original file
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File tempFile = new File("all/files/temp.txt");
        File outputFile = new File(filename);
        if (outputFile.exists())
            outputFile.delete();
        if (tempFile.renameTo(outputFile)) {
            System.out.println("File updated successfully!");
        } else {
            System.out.println("Failed to update the file!");
        }
    }

  
    public void writeHashMapToFile(HashMap<String, ArrayList<String>> hashMap, BufferedWriter writer) throws IOException {
        for (Map.Entry<String, ArrayList<String>> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> values = entry.getValue();

            writer.write(key);
            writer.newLine();

            for (String value : values) {
                writer.write(value);
                writer.newLine();
            }
            writer.newLine();
        }
    }

}
