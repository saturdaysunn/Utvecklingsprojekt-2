package all.klient.controller;
import all.jointEntity.ImageMessage;
import all.jointEntity.Message;
import all.jointEntity.User;
import all.klient.CallBackInterface;
import all.klient.boundary.MainFrame;
import all.server.controllerAndBoundary.FileController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Responsible for client side of the connection
 */

public class MessageClient extends Thread {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private MainFrame mainFrame;
    private User user;
    private FileController fileController;
    private ArrayList<String> contacts;

    public MessageClient(String ip, int port){
        try{
            socket = new Socket(ip, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            this.mainFrame = new MainFrame(1000, 600, this); //create gui for client
            new Listener().start(); //start listening for messages from server
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * creates instance of User and informs server that user has logged in
     */
    public void sendLoginMessage(String username, ImageIcon userPicture) {
        this.user = new User(username, userPicture);
        retrieveContacts();
        try {
            oos.writeObject(user);
            oos.flush();
            System.out.println("informing server that " + user.getUsername() + " has logged in");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * retrieves user's contacts from
     */ //TODO: this should be done in the server side, sent to client as arraylist
    public void retrieveContacts() {
        fileController = new FileController();
        contacts = fileController.getContactsOfUser("all/files/contacts.txt", user.getUsername());
        if (!contacts.isEmpty()) {
            this.mainFrame.getMainPanel().getrPanel().populateRPanel(contacts);
        }
    }

    /**
     * @return MainFrame instance for this client
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    private class Listener extends Thread {

        @Override
        public synchronized void run() {
            try {
                ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                while (true) {
                    Object receivedObject = ois.readObject();
                    if (receivedObject instanceof Message) {
                        Message receivedMessage = (Message) receivedObject;
                        System.out.println("Received a message: " + receivedMessage.getText());
                        //TODO: invoke callback for gui to display message
                    } else {

                        System.out.println("Received unknown object type: " + receivedObject.getClass());
                    }
                }
            } catch (IOException e) {
                System.err.println("IOException occurred: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("ClassNotFoundException occurred: " + e.getMessage());
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    System.err.println("Error closing ObjectInputStream: " + e.getMessage());
                }
            }

        }

    }


    //TODO: handle receivers & groupchat situation
    /**
     * sends text message from client to server
     * @param message message
     * @param receivers names of selected receivers
     */
    public void sendTextMessage(String message, ArrayList<String> receivers) {
        Message newMessage = new Message(user, receivers, message, null, null);
        try {
            oos.writeObject(newMessage); //write to server through stream
            System.out.println("i am trying to send: " + newMessage.getText());
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * sends image message from client to server
     * @param message full text message
     * @param file file of image to create image icon
     * @param receivers list of receivers
     */
    public void sendImageMessage(String message, File file, ArrayList<String> receivers, String imgFileName) {
        System.out.println("image message arrived in messageClient");
        ImageIcon image = new ImageIcon(file.getPath()); //create image icon from file
        loadImage(file, imgFileName); //save to sent_pictures package

        Message imageMessage = new ImageMessage(user, receivers, message, null, null, image);
        try {
            oos.writeObject(imageMessage); //write to server through stream
            System.out.println("i am trying to send: " + imageMessage.getText());
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * part of logic to save image to sent_pictures package
     * @param file image file
     * @param imageName name of image file
     */
    private void loadImage(File file, String imageName) {
        try {
            BufferedImage image = ImageIO.read(file);
            saveImage(image, imageName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * saves image to be sent to sent_pictures to be retrieved later by receiver
     * @param image image itself
     * @param imageName file name
     */
    private void saveImage(BufferedImage image, String imageName) {
        try {
            String path = "all/sent_pictures/" + imageName + ".png";
            File outputfile = new File(path);
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
