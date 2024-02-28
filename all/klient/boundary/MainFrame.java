package all.klient.boundary;

import all.klient.controller.*;
import all.server.controllerAndBoundary.FileController;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainFrame extends JFrame {
    private MainPanel panel;
    private FileController fileController = new FileController(); //TODO: initialize inside constructor?
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


    /**
     * sends message info to messageClient
     * @param message message in string format
     */
    public void sendMessage(String message) {
        System.out.println("message arrived in mainframe");
        ArrayList<String> receivers = panel.getlPanel().getReceivers(); //retrieve selected user to send message to

        if (receivers.isEmpty()) { //if no receiver has been selected
            JOptionPane.showMessageDialog(null, "No receiver has been selected");
        } else {
            if (message.contains(".png") | message.contains(".jpeg") | message.contains(".jpg")) { //check if text contains image
                System.out.println("it's an image message");

                String imgPath = extractImage(message); //extract image path from message
                File imgFile = new File(imgPath); //create file from path to create imageIcon

                String imgFileString = modify(imgPath); //shorten image path to file name
                String modifiedMessage = message.replace(imgPath, imgFileString); //modify string message

                this.messageClient.sendImageMessage(modifiedMessage, imgFile, receivers, imgFileString);
            } else {
                System.out.println("it's a text message");
                this.messageClient.sendTextMessage(message, receivers);
            }
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

    /**
     * retrieves all users from given filepath
     * @param filePath path of given file
     * @return LinkedList of all users
     */
    public LinkedList<String> retrieveAllUsersFromFile(String filePath) {
        return fileController.retrieveAllUsersFromFile(filePath);
    }

    public void removeDataBlock(String filePath, String targetString) throws IOException {
        fileController.removeTargetContent(filePath, targetString);
    }

    public List<String> getContactsOfUser(String filepath, String user){
        return fileController.getContactsOfUser(filepath, user);
    }

    public void writeHashMapToFile(HashMap<String, ArrayList<String>> hashMap, String filePath) {
        fileController.rewriteContactsTextFileWithNewContacts(hashMap, filePath);
    }

    public void saveUserToFile(String userName, String filePath) {
        fileController.saveUserToFile(userName, filePath);
    }

    public void updateOnlineList(ArrayList<String> onlineUsers){
        panel.getlPanel().updateOnlineList(onlineUsers);
    }

}
