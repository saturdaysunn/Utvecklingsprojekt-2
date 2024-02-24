package all.klient.controller;

import all.klient.view.MainFrame;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * takes care of reading and writing to files (contacts, messages, etc)
 */
public class UserController {

    public UserController() {

    }

    public void appendUserToFile(String text, String filePath) {

        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(text);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public LinkedList<String> readFromFile(String filePath) {
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

    public void writeHashMapToFile(HashMap<String, ArrayList<String>> hashMap, String filename) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, ArrayList<String>> getContactsOfUser(String filePath, String user) {
        HashMap<String, ArrayList<String>> dataMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentKey = null;
            ArrayList<String> currentList = new ArrayList<>();
            boolean emptyRowEncountered = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    emptyRowEncountered = true;
                } else {
                    if (line.contains(user) && emptyRowEncountered) {
                        if (currentKey != null) {
                            dataMap.put(currentKey, currentList);
                            currentList = new ArrayList<>();
                        }
                        currentKey = line.trim();
                        emptyRowEncountered = false;
                    } else {
                        currentList.add(line.trim());
                    }
                }
            }

            // Add last set of data
            if (currentKey != null && !currentList.isEmpty()) {
                dataMap.put(currentKey, currentList);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataMap;
    }


    public List<String> getDataAfterEmptyRow(String filePath, String searchString) {
        List<String> data = new ArrayList<>();
        boolean foundEmptyRow = false;
        boolean foundSearchString = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    foundEmptyRow = true;
                    continue;
                }

                if (foundEmptyRow && line.equals(searchString)) {
                    foundSearchString = true;
                    continue; //Skip the search string itself
                }

                if (foundSearchString) {
                    if (line.isEmpty()) {
                        //Stop reading when an empty row is encountered after finding the search string
                        break;
                    } else {
                        System.out.println(line);
                        data.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }




    //TODO: ask how this is supposed to send messages to the MessageClients (how to use callback for this?)
    public void sendMessage(String message, ImageIcon image, String senderName, ArrayList<String> receivers) {
        //TODO: send message to online client
        //TODO: or store message in files for offline client


        //TODO: change message to Message obj and username to User (?)
        //TODO: add date and(?) time
    }
}
