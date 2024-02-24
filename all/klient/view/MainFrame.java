package all.klient.view;

import all.klient.controller.*;
import javax.swing.*;
import all.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainFrame extends JFrame {
    private JFrame frame;
    private MainPanel panel;
    private UserController userController = new UserController();
    private Controller controller;
    private int width = 1000;
    private int height = 600;
    private LoginPanel loginPanel;

    public MainFrame(int width, int height, Controller c){
        super("Chatt");
        this.setResizable(false);
        this.setSize(width, height);
        this.panel = new MainPanel(width, height, this);
        this.setContentPane(panel);
        this.setVisible(true);
        this.setBounds(100, 100, width, height);
        this.setDefaultCloseOperation(3);
        controller = c;
        setupLogin();
    }

    /**
     * Initiates and calls to set up login window
     */
    public void setupLogin() {
        loginPanel = new LoginPanel(this); //create new login window
        loginPanel.setUpWindow(); //call to set up
        System.out.println("login panel started");
    }

    /**
     * displays users added to contacts (do not have to be online)
     * @param contactsArray array of contacts
     */
    public void populateRPanel(List<String> contactsArray) {
        panel.getrPanel().populateRPanel(contactsArray);
    }

    /**
     * displays online users
     * @param onlineArray array containing online users
     */
    public void populateLPanel (List<String> onlineArray) {
        panel.getlPanel().populateLPanel(onlineArray);
    }

    public void appendUserToFile(String username){
        userController.appendUserToFile(username, "all/files/users.txt");
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
        userController.sendMessage(message, image, stringUser, receivers);

    }

    public MainPanel getMainPanel() {
        return panel;
    }

    public LoginPanel getLoginPanel(){
        return loginPanel;
    }

    public LinkedList<String> readFromFile(String filePath) {
        return userController.readFromFile(filePath);
    }

    public boolean checkIfUserAlreadyExists(String username) {

        return controller.checkIfUserAlreadyExists(username, "all/files/users.txt");

    }

    public List<String> getDataAfterEmptyRow(String filePath, String searchString) {
        return userController.getDataAfterEmptyRow(filePath, searchString);
    }

    public HashMap<String, ArrayList<String>> getContactsOfUser(String filepath, String user){
        return userController.getContactsOfUser(filepath, user);
    }

    public void writeHashMapToFile(HashMap<String, ArrayList<String>> hashMap, String filePath) {
        userController.writeHashMapToFile(hashMap, filePath);
    }
}
