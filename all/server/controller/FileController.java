package all.server.controller;

import all.jointEntity.Message;
import all.jointEntity.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * takes care of reading from and writing to files (contacts, messages, users)
 */
public class FileController {
    //TODO: should all these methods be synchronized?
    //TODO: or synchronized where these are called.

    /**
     * checks if user has logged in previously.
     * @param // username name of user
     * @param filePath path of file to check
     * @return true if user exists, false if not
     */
    public boolean checkIfUserAlreadyExists(String username, String filePath) {
        LinkedList<String> users = retrieveAllUsersFromFile(filePath); //retrieve all stored usernames
        for (String user : users){
            if (user.equals(username)){ //if user has logged in before
                return true;
            }
        }
        return false;
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

    /**
     * saves new user to file containing usernames
     * @param userName name of user
     * @param filePath path of file to write to
     */
    public void saveUserToFile(String userName, String filePath) {
        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(userName);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }


    public ArrayList<String> getContactsOfUser(String filePath, String userName) {
        ArrayList<String> resultList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean usernameFound = false;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() && usernameFound) { //if next line is empty after username has been found
                    break; //all contacts have been found
                }
                if (usernameFound && !line.isEmpty()) { //username found, add following contacts
                    resultList.add(line);
                }
                if (line.equals(userName)) { //username has been found
                    usernameFound = true; // Set usernameFound to true when the search string is usernameFound
                }

                //tiffany --> username found
                //bruna --> not empty, add contact
                //mihail --> not empty, add contact
                //       --> empty 

            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException appropriately
        }
        return resultList;
    }

    /**
     * This method removes the target content from a text file while keeping the
     * remaining data intact.
     * @param filePath file path
     * @param targetString search for this string in text file
     * @throws IOException error
     */
    public void removeTargetContent(String filePath, String targetString) throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new FileReader(filePath));
            StringBuilder contentBuilder = new StringBuilder();

            String line;
            boolean isInBlock = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (isInBlock) {
                        isInBlock = false; //end of block
                    }
                } else if (line.contains(targetString)) { // Check if line contains target string
                    isInBlock = true; //start of the block
                }

                if (!isInBlock) {
                    //append line to contentBuilder if it's not within the block
                    contentBuilder.append(line);
                    contentBuilder.append(System.lineSeparator());
                }
            }

            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(contentBuilder.toString());
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

    /** //
     * stores unsent messages in a .dat file for later retrieval.
     * @param unsentMessages hashmap containing unsent messages for different offline users.
     */ //TODO: NOT YET TESTED
    public void storeUnsentMessages(HashMap<String, ArrayList<Message>> unsentMessages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("all/files/unsentMessages.dat"))) {
            for (String receiver : unsentMessages.keySet()) {
                oos.writeObject(receiver); //name of receiver

                ArrayList<Message> messages = unsentMessages.get(receiver);
                for (Message message : messages) {
                    oos.writeObject(message); //write message object to file
                }
                oos.writeObject(""); //add blank line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * retrieves unsent messages from .dat file and inserts into HashMap.
     * @return HashMap containing unread messages for different users.
     */ //TODO: NOT YET TESTED
    public HashMap<String, ArrayList<Message>> retrieveUnsentMessages() {
        HashMap<String, ArrayList<Message>> unsentMessagesMap = new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("all/files/unsentMessages.dat"))) {
            while (true) {
                //TODO: not sure if correct approach or if it works
                String receiver = (String) ois.readObject();
                if (receiver.isEmpty()) {
                    break;
                }
                ArrayList<Message> messages = new ArrayList<>();
                while (true) {
                    Message message = (Message) ois.readObject();
                    if (message == null) {
                        break;
                    }
                    messages.add(message);
                }
                unsentMessagesMap.put(receiver, messages);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return unsentMessagesMap;
    }











    //TODO: we might not need to save images to the server in order to be able to see them.

    /**
     * part of logic to save image to sent_pictures package
     * @param file image file
     * @param imageName name of image file
     */ /*
    public void loadImage(File file, String imageName) {
        try {
            BufferedImage image = ImageIO.read(file);
            saveImage(image, imageName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * saves image to be sent to sent_pictures to be retrieved later by receiver
     * @param image image itself
     * @param imageName file name
     */ /*
    public void saveImage(BufferedImage image, String imageName) {
        try {
            String path = "all/sent_pictures/" + imageName + ".png";
            File outputfile = new File(path);
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */


}
