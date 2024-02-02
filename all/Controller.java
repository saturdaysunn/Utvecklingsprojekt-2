package all;

import all.jointEntity.User;
import all.klient.controller.MessageClient;
import all.klient.view.MainFrame;

import javax.swing.*;
import java.util.ArrayList;

public class Controller {

    private User user;
    private MainFrame view;
    private ArrayList<User> userList = new ArrayList<>();

    public Controller(){

        view = new MainFrame(1000, 600, this);
        sendUserInfo();

    }

    public void populateRightPanel(String[] contactsString) {
        view.populateRPanel(contactsString);

    }
    public void sendUserInfo(){

        User bruna = new User("bruna", new ImageIcon("all/files/bruna.png"), false);
        User mihail = new User("mihail", new ImageIcon("all/files/mihail.png"), false);
        User tiffany = new User("tiffany", new ImageIcon("all/files/tiffany.png"), false);
        User oliver = new User("oliver", new ImageIcon("all/files/oliver.png"), false);

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
