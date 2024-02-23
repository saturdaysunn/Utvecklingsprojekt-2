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
