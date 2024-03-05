package all.server.controller;

import all.jointEntity.Message;

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


    /**
     * retrieves contacts of a given user
     * @param filePath path to file to read from
     * @param userName name of user whose contacts we want to retrieve
     * @return list of contacts for given user.
     */
    public ArrayList<String> getContactsOfUser(String filePath, String userName) {
        ArrayList<String> result = new ArrayList<>();
        boolean emptyRowFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    emptyRowFound = true;

                } else if (emptyRowFound && line.contains(userName)) {
                    //found the username with an empty row before it
                    while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                        result.add(line);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * This method removes the target content from a text file while keeping the
     * remaining data intact.
     * @param filePath file path
     * @param targetString search for this string in text file
     * @throws IOException error
     */
    public void removePreviousContactsInTextFileOfUser(String filePath, String targetString) throws IOException {
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

    public boolean checkIfUserAlreadyHasContacts(String filePath, String target) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean found = false;
            boolean previousLineEmpty = false;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    previousLineEmpty = true;//the line before the string is empty
                } else if (line.equals(target) && previousLineEmpty) {
                    found = true; //the target string is found, i.e. it equals the targetString and it has an empty row before it.
                    break;
                } else {
                    previousLineEmpty = false; //the line before the string is not empty
                }
            }

            if (found) {
                return true; //user was found
            } else {
                return false; //user was not found
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return false;
        }
    }

    public synchronized void rewriteContactsTextFileWithNewContacts(HashMap<String, ArrayList<String>> hashMap) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("all/files/contacts.txt"))) {

            Set<String> keys = hashMap.keySet();
            String[] keysArray = keys.toArray(new String[0]);
            System.out.println("Keys Array pos 0: " + keysArray[0]);

            if(checkIfUserAlreadyHasContacts("all/files/contacts.txt", keysArray[0])){
                System.out.println("User already has contacts...");

                removePreviousContactsInTextFileOfUser("all/files/contacts.txt", keysArray[0]);

                writer.newLine(); //add an empty line before writing the HashMap
                writeHashMapToFile(hashMap, writer);

            } else if (!checkIfUserAlreadyHasContacts("all/files/contacts.txt", keysArray[0])){ //else if the user doesn't have already have contacts then just append to the file
                System.out.println("User doesn't have contacts.");
                writeHashMapToFile(hashMap, new BufferedWriter(new FileWriter("all/files/contacts.txt", true)));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  
    public void writeHashMapToFile(HashMap<String, ArrayList<String>> hashMap, BufferedWriter writer) throws IOException {

        if (!hashMap.values().isEmpty()){
            for (Map.Entry<String, ArrayList<String>> entry : hashMap.entrySet()) {
                String key = entry.getKey();
                ArrayList<String> values = entry.getValue();

                writer.write(key);
                writer.newLine();

                for (String value : values) {
                    writer.write(value);
                    System.out.println(value + " is being added to text file");
                    writer.newLine();
                }
                writer.newLine();
            }
        } else {
            System.err.println("No contacts to write to file! //FileController writeHashMapToFile");
        }
    }


    /** //
     * stores unsent messages in a .dat file for later retrieval.
     * @param unsentMessages hashmap containing unsent messages for different offline users.
     */ //TODO: NOT YET TESTED OR PROPERLY IMPLEMENTED
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

}
