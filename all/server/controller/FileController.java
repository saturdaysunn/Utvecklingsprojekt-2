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
     * @param username username name of user
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
     * @param username name of user whose contacts we want to retrieve
     * @return list of contacts for given user.
     */
    public synchronized ArrayList<String> getContactsOfUser(String filePath, String username) {
        ArrayList<String> contactsOfUser = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            boolean foundUsername = false;
            String line;

            while ((line = reader.readLine()) != null) {
                //check if the line contains the username with a dot above it
                if (line.equals(".") && foundUsername) { //if both found
                    break; //stop reading after encountering an empty row after the username
                }
                if (line.equals(username) && !foundUsername) {
                    foundUsername = true;
                } else if (foundUsername) {
                    contactsOfUser.add(line);
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contactsOfUser;
    }

    /**
     * This method removes the target content from a text file while keeping the
     * remaining data intact.
     * @param fileName file path
     * @param username search for this string in text file
     * @throws IOException error
     */
    public synchronized static void removePreviousContactsInTextFileOfUser(String username, String fileName) {
        File inputFile = new File(fileName);
        File tempFile = new File("all/files/temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            boolean foundDot = false;
            boolean foundString = false;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(".")) {
                    if (foundString) {
                        foundString = false;
                    } else {
                        foundDot = true;
                    }
                } else if (foundDot) {
                    if (line.contains(username)) {
                        foundString = true;
                        foundDot = false;
                    }
                } else if (!foundString) {
                    writer.write(line + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Delete the original file
        inputFile.delete();

        // Rename the temporary file to the original file name
        tempFile.renameTo(inputFile);
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

    public synchronized void rewriteContactsTextFileWithNewContacts(HashMap<String, ArrayList<String>> hashMap) throws IOException {

        Set<String> keys = hashMap.keySet();
        String[] keysArray = keys.toArray(new String[0]);
        System.out.println("Keys Array pos 0: " + keysArray[0]);

        if(checkIfUserAlreadyHasContacts("all/files/contacts.txt", keysArray[0])){
            System.out.println("User already has contacts...");

            removePreviousContactsInTextFileOfUser(keysArray[0], "all/files/contacts.txt"); //TODO doesn't work
            writeHashMapToFile(hashMap);

        } else if (!checkIfUserAlreadyHasContacts("all/files/contacts.txt", keysArray[0])){ //else if the user doesn't have already have contacts then just append to the file
            System.out.println("User doesn't have contacts.");
            writeHashMapToFile(hashMap);

        }
    }

  
    public static void writeHashMapToFile(HashMap<String, ArrayList<String>> contacts) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("all/files/contacts.txt", true))) {
            for (String key : contacts.keySet()) {
                writer.write(".");
                writer.newLine();
                writer.write(key);
                writer.newLine();

                ArrayList<String> values = contacts.get(key);
                for (String value : values) {
                    writer.write(value);
                    writer.newLine();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /** //
     * stores unsent messages in a .dat file for later retrieval.
     * @param unsentMessages hashmap containing unsent messages for different offline users.
     */ //TODO: NOT YET TESTED OR PROPERLY IMPLEMENTED
    public void storeUnsentMessages(HashMap<String, ArrayList<Message>> unsentMessages) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("all/files/unsentMessages.dat"))) {
            for (Map.Entry<String, ArrayList<Message>> entry : unsentMessages.entrySet()) {
                String key = entry.getKey();
                ArrayList<Message> values = entry.getValue();

                if (values != null) {
                    outputStream.writeObject(key);
                    for (Message value : values) {
                        outputStream.writeObject(value);
                    }
                    outputStream.writeObject(new Message(null,null, "", null, null)); //writing an empty string to indicate the end
                }
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
        HashMap<String, ArrayList<Message>> data = new HashMap<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("all/files/unsentMessages.dat"))) {
            while (true) {
                String key = inputStream.readUTF();
                ArrayList<Message> messages = new ArrayList<>();
                boolean foundEmptyMessage = false;
                while (!foundEmptyMessage) {
                    Message message = (Message) inputStream.readObject();
                    if (message.getSender().getUsername() == null && message.getText().isEmpty()) {
                        foundEmptyMessage = true;
                    } else {
                        messages.add(message);
                    }
                }
                data.put(key, messages);
            }
        } catch (EOFException e) {
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

}
