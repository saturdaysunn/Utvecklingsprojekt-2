package all;

import all.jointEntity.User;
import all.klient.controller.MessageClient;
import all.klient.view.MainFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Controller {

    private User user;
    private MainFrame view;
    private ArrayList<User> userList = new ArrayList<>();

    public Controller(){
        view = new MainFrame(1000, 600, this);
    }

    public boolean checkIfUserAlreadyExists(String username, String filePath) {

        LinkedList<String> users = view.readFromFile(filePath);
        boolean outcome = false;

        for (String user : users){
            if (user.equals(username)){
                JOptionPane.showMessageDialog(null, "User already exists!");
                List<String> contactsOfUser = view.getContactsOfUser("all/files/contacts.txt", username);
                view.populateRPanel(contactsOfUser); //Populates the right panel with the contacts of the user
                //TODO load chats for the user
                outcome = true;
                break;
            }
        }
        return outcome;
    }


    public void userLoggedIn(String userName){
        for(User user : userList){
            if(user.getUsername().equals(userName)){
                user.setOnline(true);
                new MessageClient("bla bla", 200).start();
                // starta klienten
            }
        }
    }




}
