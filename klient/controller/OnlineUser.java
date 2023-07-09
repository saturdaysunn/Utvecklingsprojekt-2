package klient.controller;

import jointEntity.User;
import klient.view.MainFrame;

import java.util.ArrayList;

public class OnlineUser {
    //stores users which are online
    private ArrayList<User> onlineUsers;
    private ClientController controller;
    private MainFrame view;




    public void contactsToString(ArrayList<User> onlineUsers) {

        String[] stringContactList = new String[onlineUsers.size()];

        view.populateRPanel(stringContactList);
    }

}
