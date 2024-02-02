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

        sendUserInfo();

    }

    public void populateRightPanel(String[] contactsString) {
        view.populateRPanel(contactsString);

    }
    public void sendUserInfo(){

        User bruna = new User("brunabrub", new ImageIcon("files/list-pochacco.png"), false);
        User mihail = new User("mihail", new ImageIcon("/"), false);
        User tiffany = new User("tiffany", new ImageIcon("/"), false);
        User oliver = new User("oliver", new ImageIcon("/"), false);

        view.setUpWindow(bruna.getUsername(), bruna.getIcon());
        view.setUpWindow(mihail.getUsername(), mihail.getIcon());
        view.setUpWindow(tiffany.getUsername(), tiffany.getIcon());
        view.setUpWindow(oliver.getUsername(), oliver.getIcon());

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
