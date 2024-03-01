package all.klient.controller;
import all.jointEntity.*;
import all.klient.boundary.MainFrame;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

/**
 * Responsible for client side logic
 */
public class MessageClient extends Thread {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private MainFrame mainFrame;
    private User user;
    private ArrayList<String> contacts;
    private ArrayList<String> onlineUsers;
    private Listener listener;

    public MessageClient(String ip, int port){
        try{
            socket = new Socket(ip, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            this.mainFrame = new MainFrame(1000, 600, this); //create gui for client
            listener = new Listener();
            listener.start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * checks the status of the object that was received.
     * @param receivedObject object that was received through stream.
     */
    public void checkObjectStatus(Object receivedObject) {
        System.out.println("client going to check obj status");
        if (receivedObject instanceof Message) {
            System.out.println("i've received a message");
            Message receivedMessage = (Message) receivedObject;
            System.out.println("Received a message: " + receivedMessage.getText());
            receivedMessage.setDeliveredTime(new Date()); //add time delivered to receiver
            mainFrame.newMessage(receivedMessage);
        } else if (receivedObject instanceof ContactsMessage) {
            ContactsMessage contactsMessage = (ContactsMessage) receivedObject;
            contacts = contactsMessage.getContactsList();
            if (contacts == null) { //if doesn't exist.
                System.out.println("received contacts list is null, creating new one");
                contacts = new ArrayList<>(); //create new one
            }
            mainFrame.updateContactsList(contacts);
        } else if (receivedObject instanceof ArrayList<?>) {
            onlineUsers = (ArrayList<String>) receivedObject;
            mainFrame.updateOnlineList(onlineUsers);
        } else if (receivedObject instanceof UnsentMessages) {
            //TODO: handle unsent messages. what to do with them?
            //TODO: same as for other messages? send to mainframe to temp store?
        }
    }

    /**
     * creates instance of User and informs server that user has logged in
     */
    public void sendLoginMessage(String username, ImageIcon userPicture) {
        this.user = new User(username, userPicture);
        this.listener.sendLoginmessage(this.user);
    }

    //TODO: handle checks so it's sent to the correct person.
    /**
     * handles logic for creating instance of message to send to server
     * @param message full text message
     * @param receivers list of receivers
     */
    public void sendMessage(String message, ArrayList<String> receivers) {
        System.out.println("message in sender's client");

        if (message.contains(".png") | message.contains(".jpg")) { //if text contains image
            System.out.println("it's an image message");

            String imgPath = extractImagePath(message); //extract image path from message
            File imgFile = new File(imgPath); //create file from path to create imageIcon
            ImageIcon image = new ImageIcon(imgFile.getPath()); //create image icon from file

            String imgFileString = modify(imgPath); //shorten image path
            String modifiedMessage = message.replace(imgPath, imgFileString); //modify string message

            Message imageMessage = new ImageMessage(this.user, receivers, modifiedMessage,
                    null, null, image);

            listener.sendMessage(imageMessage); //send message to client boundary
        } else {
            System.out.println("it's a text message");
            Message textMessage = new Message(this.user, receivers, message, null, null);
            listener.sendMessage(textMessage);
        }
    }

    /**
     * extracts image path from whole message
     * @param message whole message to be sent
     * @return image path in string format
     */
    public String extractImagePath(String message) {
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
     * adds new contact to user's contact list if not already there
     * @param userToAdd name of user
     */
    public void addToContacts(String userToAdd) {
        boolean alreadyContact = false;
        for (String contact : contacts) {
            if (contact.equals(userToAdd)) {
                alreadyContact = true;
                break;
            }
        }

        if (alreadyContact) {
            JOptionPane.showMessageDialog(null, "This user is already in your contacts");
        } else {
            user.addContact(userToAdd); //add to user's contacts list
            contacts.add(userToAdd);
            System.out.println("added " + userToAdd + " to contacts");
            mainFrame.updateContactsList(contacts); //update on GUI
        }
    }

    /**
     * @return MainFrame instance for this client
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    //TODO: should store info and send to server somehow?
    //TODO: or, how should it be saved. do we need a dat file?
    /**
     * should save user info upon logout?
     */
    public void saveUserInfo() {
        this.listener.sendLogoutMessage(user.getUsername());
    }




    /**
     * inner class responsible for communication between client and server
     */
    private class Listener extends Thread {

        @Override
        public synchronized void run() {
            try {
                ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                while (true) {
                    Object receivedObject = ois.readObject();
                    checkObjectStatus(receivedObject);
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


        /**
         * sends message to server that user has logged in.
         * @param user instance of user than logged in
         */
        public void sendLoginmessage(User user) {
            try {
                oos.writeObject(user);
                oos.flush();
                System.out.println("informing server that " + user.getUsername() + " has logged in");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * sends message to server that user has logged out.
         * @param username name of user that logged out.
         */
        public void sendLogoutMessage(String username) {
            try {
                oos.writeObject(username);
                oos.flush();
                System.out.println("informing server that " + username + " has logged out");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        /**
         * sends message object to server.
         * @param newMessage new message object
         */
        public void sendMessage(Message newMessage) {
            try {
                oos.writeObject(newMessage);
                System.out.println("i am trying to send: " + newMessage.getText());
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
