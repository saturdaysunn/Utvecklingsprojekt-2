package all.klient.controller;
import all.jointEntity.ImageMessage;
import all.jointEntity.Message;
import all.jointEntity.User;
import all.klient.CallBackInterface;
import all.klient.view.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

/**
 * The class initiates a connection between the client and the server
 *
 */

public class MessageClient extends Thread {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private ArrayList<CallBackInterface> listeners = new ArrayList<>();
    private MainFrame mainFrame;

    public MessageClient(String ip, int port){
        try{
            socket = new Socket(ip, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            this.mainFrame = new MainFrame(1000, 600, this); //create new frame for client
            sendLoginMessage(); //inform server that user has logged in.
            new Listener().start(); //start listening for messages from server
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * @return MainFrame instance for this client
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * creates instance of User and informs server that user has logged in
     */
    public void sendLoginMessage() {
        //TODO: use sleep or checking condition here?
        // to make sure they have been set before creating user and sending
        String username = mainFrame.getMainPanel().getrPanel().getCurrentUsername();
        ImageIcon userPicture = mainFrame.getMainPanel().getrPanel().getUserIcon();
        User user = new User(username, userPicture); //create new instance of user

        try {
            oos.writeObject(user); //write to server through stream
            oos.flush();
            System.out.println("informing server that " + user.getUsername() + " has logged in");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * add listener instance to callback listener arraylist
     * @param listener instance of listener
     */ //TODO: might not need this since using streams instead
    public void addListener(CallBackInterface listener){
        listeners.add(listener);
    }

    private class Listener extends Thread implements CallBackInterface { //TODO: maybe no callback
        @Override
        public void run() {
            addListener(this); //TODO: remove?

            try {
                ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                while (true) {
                    Object receivedObject = ois.readObject();

                    if (receivedObject instanceof Message) {
                        Message receivedMessage = (Message) receivedObject;
                        System.out.println("Received a message: " + receivedMessage);
                        newMessage(receivedMessage);
                    } else {
                        System.out.println("Received unknown object type: " + receivedObject.getClass());
                    }
                    // TODO: here check for when to send private messages or group chat
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

        @Override
        public void newMessage(Message message) {
            System.out.println("new message arrived in messageClient");
            //TODO: functionality for handing receivers etc
        }
    }


    //TODO: handle receivers & groupchat situation
    /**
     * sends text message from client to server
     * @param message message
     * @param senderName name of sender
     * @param receivers names of selected receivers
     */
    public void sendTextMessage(String message, String senderName, ArrayList<String> receivers) {
        System.out.println("text message arrived in messageClient");
        Date todaysDate = new Date(); //TODO: what format?

        Message newMessage = new Message(null, null, message, todaysDate, null);
        try {
            oos.writeObject(newMessage); //write to server through stream
            System.out.println("i am trying to send: " + newMessage);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO: store to message history
        //TODO: or store message in files for offline client
        //TODO: change username to User by finding them in array/hashmap/something (?)
    }


    /**
     * sends image message from client to server
     * @param message full text message
     * @param file file of image to create image icon
     * @param senderName name of sender
     * @param receivers list of receivers
     */
    public void sendImageMessage(String message, File file, String senderName, ArrayList<String> receivers, String imgFileName) {
        System.out.println("image message arrived in messageClient");
        Date todaysDate = new Date(); //TODO: what format?
        ImageIcon image = new ImageIcon(file.getPath()); //create image icon from file
        loadImage(file, imgFileName); //to save to file

        //TODO: temp null values
        Message imageMessage = new ImageMessage(null, null, message, todaysDate, null, image);
        try {
            oos.writeObject(imageMessage); //write to server through stream
            System.out.println("i am trying to send: " + imageMessage);
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
