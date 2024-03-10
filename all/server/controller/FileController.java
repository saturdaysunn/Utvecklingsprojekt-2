package all.server.controller;

import all.jointEntity.Message;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * takes care of reading from and writing to files (contacts, messages, users)
 */
public class FileController {

    /**
     * checks if user has logged in previously.
     * @param username username name of user
     * @param filePath path of file to check
     * @return true if user exists, false if not
     */
    public synchronized boolean checkIfUserAlreadyExists(String username, String filePath) {
        LinkedList<String> users = retrieveAllUsersFromFile(filePath); //retrieve all stored usernames
        for (String user : users){
            if (user.equals(username)){ //if user has logged in before
                return true;
            }
        }
        return false;
    }

    /**
     * retrieves list of all users from file
     * @param filePath path to file
     * @return list of usernames
     */
    public synchronized LinkedList<String> retrieveAllUsersFromFile(String filePath) {
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
    public synchronized void saveUserToFile(String userName, String filePath) {
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
            String line;
            boolean foundUsername = false;

            while ((line = reader.readLine()) != null) {
                if (line.equals(".") && reader.readLine().equals(username)) {
                    foundUsername = true;
                    continue;
                }

                if (foundUsername && !line.equals(".")) {
                    contactsOfUser.add(line); //read contact until next dot is found
                } else if (foundUsername && line.equals(".")) {
                    break; //stop when next dot is found
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
     */
    public synchronized static void removePreviousContactsOfUser(String username, String fileName) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            String prevLine = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
                if (line.equals(username) && prevLine != null && prevLine.equals(".") || line.equals(username) && prevLine.isEmpty()){
                    //skip the user and their contacts
                    while ((line = br.readLine()) != null && !line.isEmpty() && !line.equals(".")) {
                        //skip
                    }
                    //skip the dot line
                    if (line != null && line.equals(".")) {
                        lines.remove(lines.size() - 1);
                    }
                }
                prevLine = line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //write the modified content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * checks if username already exists as contact owner in contacts.txt file.
     * @param filePath path of file to read
     * @param targetName //name to search for
     * @return true if exists, false if not.
     */
    public synchronized boolean checkIfUserAlreadyHasContacts(String filePath, String targetName) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean found = false;
            boolean previousLineStop = false;

            while ((line = br.readLine()) != null) {
                if (line.trim().equals(targetName) && previousLineStop) {
                    found = true;
                    break;
                }
                previousLineStop = line.trim().endsWith(".");
            }

            return found;
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return false;
        }
    }


    /**
     * rewrites contacts.txt file with new contacts for users.
     * @param owner owner of contacts
     * @param contacts list of contacts names
     */
    public synchronized void rewriteContactsTextFileWithNewContacts(String owner, ArrayList<String> contacts) {
        if(checkIfUserAlreadyHasContacts("all/files/contacts.txt", owner)){
            System.out.println("User already has contacts...");

            System.out.println(owner);
            removePreviousContactsOfUser(owner, "all/files/contacts.txt");
            writeNewContactsToFile(owner, contacts);

        } else if (!checkIfUserAlreadyHasContacts("all/files/contacts.txt", owner)){ //only append if not
            System.out.println("User doesn't have contacts...");
            writeNewContactsToFile(owner, contacts);

        } else if (contacts.isEmpty()) {
            System.out.println("No new contacts to add...");
        }
    }


    /**
     * appends contacts list for a user to contacts.txt
     * @param owner owner of contacts
     * @param contacts list of contacts names
     */
    public synchronized static void writeNewContactsToFile(String owner, ArrayList<String> contacts) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("all/files/contacts.txt", true))) {
            writer.write(".");
            writer.newLine();
            writer.write(owner); //add owner name
            writer.newLine();

            for (String contact : contacts) { //add each contact in arraylist
                writer.write(contact);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * retrieves unsent messages from .dat file and inserts into HashMap.
     * @return HashMap containing unread messages for different users.
     */
    @SuppressWarnings("unchecked")
    public synchronized HashMap<String, ArrayList<Message>> retrieveUnsentMessages() {
        HashMap<String, ArrayList<Message>> hashMap = new HashMap<>();
        try (FileInputStream fileIn = new FileInputStream("all/files/unsentMessages.dat");
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            Object obj = objectIn.readObject();
            if (obj instanceof HashMap) {
                hashMap = (HashMap<String, ArrayList<Message>>) obj;
                System.out.println("HashMap has been successfully read from " + "all/files/unsentMessages.dat");
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading HashMap from file: " + e.getMessage());
        }
        return hashMap;
    }

    /**
     * stores unsent messages in a .dat file for later retrieval.
     * @param unsentMessages hashmap of unsent messages for different receiver users.
     */
    public synchronized void updateUnsentMessages(HashMap<String, ArrayList<Message>> unsentMessages) {
        Path filePath = Paths.get("all/files/unsentMessages.dat");

        try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile());
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            objectOut.writeObject(unsentMessages);
            System.out.println("HashMap has been successfully written to " + filePath);

        } catch (IOException e) {
            System.err.println("Error writing HashMap to file: " + e.getMessage());
        }
    }


}
