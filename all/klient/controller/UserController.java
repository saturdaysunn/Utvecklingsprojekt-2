package all.klient.controller;

import javax.swing.*;
import java.util.ArrayList;

/**
 * takes care of reading and writing to files (contacts, messages, etc)
 */
public class UserController {

    public UserController() {

    }



    //TODO: ask how this is supposed to send messages to the MessageClients (how to use callback for this?)
    public void sendMessage(String message, ImageIcon image, String senderName, ArrayList<String> receivers) {
        //TODO: send message to online client
        //TODO: or store message in files for offline client


        //TODO: change message to Message obj and username to User (?)
        //TODO: add date and(?) time
    }
}
