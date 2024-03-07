package all.server.controller;

import all.jointEntity.Message;

import java.io.*;
import java.lang.reflect.Array;
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
     * @return list of contacts for given user. //TODO: this seems to work fine
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
        File inputFile = new File(fileName);
        File tempFile = new File("all/files/temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            boolean skipLines = false;

            while ((line = reader.readLine()) != null) {
                if (line.equals(".") && reader.readLine().equals(username)) {
                    skipLines = true;
                    reader.readLine();
                    continue;
                } else if (line.equals(".")) {
                    skipLines = false;
                }

                if (!skipLines) {
                    writer.write(line);
                    writer.newLine();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        //Delete the original file
        inputFile.delete();

        //Rename the temporary file to the original file name
        tempFile.renameTo(inputFile);
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


    public synchronized void rewriteContactsTextFileWithNewContacts(String owner, ArrayList<String> contacts) {
        if(checkIfUserAlreadyHasContacts("all/files/contacts.txt", owner)){ //TODO: works
            System.out.println("User already has contacts...");

            removePreviousContactsOfUser(owner, "all/files/contacts.txt"); //TODO: DOESN'T WORK
            writeNewContactsToFile(owner, contacts);

        } else if (!checkIfUserAlreadyHasContacts("all/files/contacts.txt", owner)){ //only append if not
            System.out.println("User doesn't have contacts.");
            writeNewContactsToFile(owner, contacts); //TODO: works

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
                    //TODO: What in the world is happening here?
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
