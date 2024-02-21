package all.klient.controller;

import all.jointEntity.User;
import all.klient.view.MainFrame;

import java.util.ArrayList;

public class OnlineUser { //TODO: poor naming choice, sounds like entity
    //stores users which are online
    private ArrayList<User> onlineUsers;
    //private ClientController controller;
    private MainFrame view;


    public void contactsToString(ArrayList<User> onlineUsers) {

        String[] stringContactList = new String[onlineUsers.size()];

        view.populateRPanel(stringContactList);
    }

}
