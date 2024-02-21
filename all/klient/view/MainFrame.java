package all.klient.view;

import all.klient.controller.*;
import javax.swing.*;
import all.Controller;

import java.util.ArrayList;

public class MainFrame extends JFrame {
    private JFrame frame;
    private MainPanel panel;
    private UserController controller;
    private int width = 1000;
    private int height = 600;

    public MainFrame(int width, int height, UserController controller){
        super("Chatt");
        this.setResizable(false);
        this.setSize(width, height);
        this.panel = new MainPanel(width, height, this);
        this.setContentPane(panel);
        this.setVisible(true);
        this.setBounds(100, 100, width, height);
        this.setDefaultCloseOperation(3);
        setupLogin();
    }

    /**
     * Initiates and calls to set up login window
     */
    public void setupLogin() {
        LoginPanel loginPanel = new LoginPanel(this); //create new login window
        loginPanel.setUpWindow(); //call to set up
        System.out.println("login panel started");
    }

    /**
     * displays users added to contacts (do not have to be online)
     * @param contactsArray array of contacts
     */
    public void populateRPanel(String[] contactsArray) {
        panel.getrPanel().populateRPanel(contactsArray);
    }

    /**
     * displays online users
     * @param onlineArray array containing online users
     */
    public void populateLPanel (String[] onlineArray) {
        panel.getlPanel().populateLPanel(onlineArray);
    }


    /*
    /**
     * informs controller that user has logged in
     * @param userName name of user
     */ /*
    public void userLoggedIn(String userName){
        controller.userLoggedIn(userName);
    } */

    //TODO: read sender from label on left panel (above contacts list in left panel) Maybe show picture too.
    //TODO: read receivers from checked boxes/users in right panel

    public void sendMessage(String message, ImageIcon image) {
        String stringUser = panel.getlPanel().getUsername();
        ArrayList<String> receivers = panel.getlPanel().getReceivers();
        controller.sendMessage(message, image, stringUser, receivers);

    }


}


