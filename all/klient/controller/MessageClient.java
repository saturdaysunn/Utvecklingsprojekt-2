package all.klient.controller;
import all.jointEntity.*;
import all.klient.boundary.MainFrame;

import javax.swing.*;
import java.awt.*;
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
            this.mainFrame = new MainFrame(700, 500, this); //create gui for client
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
    public synchronized void checkObjectStatus(Object receivedObject) {

        if (receivedObject instanceof Message) {
            Message receivedMessage = (Message) receivedObject;
            ArrayList<String> designatedReceivers = receivedMessage.getReceiverList();
            boolean correctlySent = false;
            for (String receiver : designatedReceivers) {
                if (receiver.equals(user.getUsername())) { //if to me
                    correctlySent = true;
                    break;
                }
            }
            if (correctlySent) { //if correct, display
                receivedMessage.setDeliveredTime(new Date()); //add time delivered to receiver
                mainFrame.tempStoreMessage(receivedMessage);
            }

        } else if (receivedObject instanceof ContactsMessage) {
            ContactsMessage contactsMessage = (ContactsMessage) receivedObject;
            contacts = contactsMessage.getContactsList();
            if (contacts == null) {
                contacts = new ArrayList<>(); //create new one
            }
            mainFrame.updateContactsList(contacts);

        } else if (receivedObject instanceof ArrayList<?>) { //list of online users
            onlineUsers = (ArrayList<String>) receivedObject;
            mainFrame.updateOnlineList(onlineUsers);

        } else if (receivedObject instanceof UnsentMessages) { //list of unsent messages
            ArrayList<Message> unsentMessages = ((UnsentMessages) receivedObject).getUnsentList();
            for (Message message : unsentMessages) {
                message.setDeliveredTime(new Date());
                mainFrame.tempStoreMessage(message);
            }
        }
    }

    /**
     * creates instance of User and informs server that user has logged in
     */
    public void sendLoginMessage(String username, ImageIcon userPicture) {
        this.user = new User(username, userPicture);
        this.listener.sendLoginMessage(this.user);
    }

    /**
     * handles logic for creating instance of message to send to server
     * @param message full text message
     * @param receivers list of receivers
     */
    public synchronized void sendMessage(String message, ArrayList<String> receivers) {
        if (message.contains(".png") | message.contains(".jpg")) { //if text contains image

            String imgPath = extractImagePath(message); //extract image path from message
            File imgFile = new File(imgPath); //create file from path to create imageIcon
            ImageIcon image = new ImageIcon(imgFile.getPath()); //create image icon from file

            String imgFileString = modify(imgPath); //shorten image path
            String modifiedMessage = message.replace(imgPath, imgFileString); //modify string message

            Message imageMessage = new ImageMessage(this.user, receivers, modifiedMessage,
                    null, null, resizeImage(image));

            listener.sendMessage(imageMessage); //send message to client boundary
            mainFrame.tempStoreOwnMessage(imageMessage);
        } else {
            Message textMessage = new Message(this.user, receivers, message, null, null);
            listener.sendMessage(textMessage);
            mainFrame.tempStoreOwnMessage(textMessage);
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
            return imgPath.substring(lastIndex + 1); //extract substring
        } else {
            return imgPath;
        }
    }

    /**
     * adds new contact to user's contact list if not already there
     * @param userToAdd name of user
     */
    public synchronized void addToContacts(String userToAdd) {
        boolean alreadyContact = false;

        if (contacts != null ) {
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
                mainFrame.updateContactsList(contacts); //update on GUI
            }
        }else{
            contacts = new ArrayList<>(); //create new one
            contacts.add(userToAdd); //add to user's contacts list
            mainFrame.updateContactsList(contacts);
        }

    }

    /**
     * @return MainFrame instance for this client
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * sends message containing name of user that logged out
     * and their updated list of contacts.
     */

    public synchronized void logOut() {
        ContactsMessage updatedContacts = new ContactsMessage(contacts);
        updatedContacts.setOwner(user.getUsername());
        if (updatedContacts.getContactsList() != null) {

            for (String contact : updatedContacts.getContactsList()) {
            }
        }
        this.listener.sendUpdatedContacts(updatedContacts);
    }

    public static ImageIcon resizeImage(ImageIcon originalIcon) {
        Image originalImage = originalIcon.getImage();
        int width = 100; // specify the desired width and height
        int height = 100;
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
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
        public void sendLoginMessage(User user) {
            try {
                oos.writeObject(user);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * sends ContactsMessage to server when user logs out.
         */
        public void sendUpdatedContacts(ContactsMessage updatedContacts) {
            try {
                oos.writeObject(updatedContacts);
                oos.flush();
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
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
