package all.klient.controller;

import all.klient.view.MainFrame;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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


    //TODO: ask how this is supposed to send messages to the MessageClients (how to use callback for this?)
    public void sendMessage(String message, String senderName, ArrayList<String> receivers) {
        //TODO: send message to online client
        //TODO: or store message in files for offline client


        //TODO: change message to Message obj and username to User (?)
        //TODO: add date and(?) time
    }
}
