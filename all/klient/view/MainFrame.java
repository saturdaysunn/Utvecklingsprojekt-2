package all.klient.view;

import all.klient.controller.*;
import javax.swing.*;
import all.Controller;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainFrame extends JFrame {
    private MainPanel panel;
    private UserController userController = new UserController(); //TODO: initialize inside constructor?
    private Controller controller; //TODO: remove this class and use UserController instead for file reading
    private LoginPanel loginPanel;
    private MessageClient messageClient;

    public MainFrame(int width, int height, MessageClient messageClient) {
        super("Chatt");
        this.setResizable(false);
        this.setSize(width, height);
        this.panel = new MainPanel(width, height, this);
        this.setContentPane(panel);
        this.setVisible(true);
        this.setBounds(100, 100, width, height);
        this.setDefaultCloseOperation(3);
        this.messageClient = messageClient;
        setupLogin(); //open login window
        //TODO: make it so that login window shows up first?
    }

    /**
     * Initiates and calls to set up login window
     */
    public void setupLogin() {
        loginPanel = new LoginPanel(this, this.messageClient); //create new login window
        loginPanel.setUpWindow(); //call to set up
        System.out.println("login panel started");
    }

    /**
     * displays users added to contacts
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

    //TODO: don't think we need this here
    // only needed when new user logs in to store all users
    public void saveUserToFile(String username){
        userController.saveUserToFile(username, "all/files/users.txt");
    }

    /**
     * sends message info to messageClient
     * @param message message in string format
     */
    public void sendMessage(String message) {
        System.out.println("message arrived in mainframe");
        String stringUser = panel.getlPanel().getUsername(); //username of current user
        ArrayList<String> receivers = panel.getlPanel().getReceivers(); //retrieve selected user to send message to

        if (message.contains(".png") | message.contains(".jpeg") | message.contains(".jpg")) { //check if text contains image
            System.out.println("it's an image message");

            String imgPath = extractImage(message); //extract image path from message
            File imgFile = new File(imgPath); //create file from path to create imageIcon

            String imgFileString = modify(imgPath); //shorten image path to file name
            String modifiedMessage = message.replace(message, imgFileString); //modify string message

            this.messageClient.sendImageMessage(modifiedMessage, imgFile, stringUser, receivers, imgFileString);
        } else {
            System.out.println("it's a text message");
            this.messageClient.sendTextMessage(message, stringUser, receivers);
        }
    }

    /**
     * extracts image path from whole message
     * @param message whole message to be sent
     * @return image path in string format
     */
    public String extractImage(String message) {
        String[] words = message.split("\\s+");
        String imageFileName = "";

        for (String word : words) {
            if (word.toLowerCase().endsWith(".jpeg") || word.toLowerCase().endsWith(".png") || word.toLowerCase().endsWith(".jpg")) {
                imageFileName = word;
                break;
            }
        }
        System.out.println("Image path: " + imageFileName);
        return imageFileName;
    }

    /**
     * extracts file name from image path
     * @param imgPath full image path
     * @return file name of image
     */
    public String modify(String imgPath) {
        int lastIndex = imgPath.lastIndexOf('/');
        if (lastIndex != -1) { //if '/' character is found
            System.out.println("image file: " + imgPath.substring(lastIndex + 1));
            return imgPath.substring(lastIndex + 1); //extract substring
        } else {
            return imgPath;
        }
    }


    /**
     * @return instance of mainPanel
     */
    public MainPanel getMainPanel() {
        return panel;
    }

    public LinkedList<String> readFromFile(String filePath) {
        return userController.retrieveAllUsersFromFile(filePath);
    }

    //TODO: change to userController instead of controller
    /*
    public boolean checkIfUserAlreadyExists(String username) {
        return controller.checkIfUserAlreadyExists(username, "all/files/users.txt");
    } */

    /**
     * informs controller that user has logged in
     * @param userName name of user
     */ //TODO: don't think we need this either, online status should be handled in messageClient/Server
    public void userLoggedIn(String userName){
        controller.userLoggedIn(userName);
    }


    //TODO: vague name
    /*
    public List<String> getDataAfterEmptyRow(String filePath, String searchString) {
        return userController.getDataAfterEmptyRow(filePath, searchString);
    }
    */


    public void removeDataBlock(String filePath, String targetString, String outputFilePath) throws IOException {
        userController.removeDataBlock(filePath, targetString, outputFilePath);

    }

    public List<String> getContactsOfUser(String filepath, String user){
        return userController.getContactsOfUser(filepath, user);
    }

    //TODO: vague name
    public void writeHashMapToFile(HashMap<String, ArrayList<String>> hashMap, String filePath) {
        userController.rewriteContactsTextFileWithNewContacts(hashMap, filePath);
    }
}
