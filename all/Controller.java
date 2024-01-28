package all;

import all.jointEntity.User;
import all.klient.controller.MessageClient;
import all.klient.entity.Client;
import all.klient.view.MainFrame;

import javax.swing.*;
import java.util.ArrayList;

public class Controller {

    private User user;
    private MainFrame view;
    private ArrayList<User> userList = new ArrayList<>();

    public Controller(){



    }

    public void populateRightPanel(String[] contactsString) {
        view.populateRPanel(contactsString);

    }
    public ArrayList<User> displayAvailableUsers(){

        User bruna = new User("brunabrub", new ImageIcon("files/list-pochacco.png"), false);
        User mihail = new User("mihail", new ImageIcon("/"), false);
        User tiffany = new User("tiffany", new ImageIcon("/"), false);
        User oliver = new User("oliver", new ImageIcon("/"), false);

        userList.add(bruna);
        userList.add(mihail);
        userList.add(tiffany);
        userList.add(oliver);

        return userList;

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
