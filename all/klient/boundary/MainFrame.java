package all.klient.boundary;

import all.jointEntity.Message;
import all.klient.controller.*;
import all.server.controller.FileController;

import javax.swing.*;

import java.util.ArrayList;

public class MainFrame extends JFrame {
    private MainPanel panel;
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
     * controls so receivers have been selected and sends message input to MessageClient if yes.
     * @param message message input through GUI.
     */
    public void sendMessage(String message) {
        ArrayList<String> receivers = panel.getlPanel().getReceivers(); //retrieve selected user to send message to

        if (receivers.isEmpty()) { //if no receiver has been selected
            JOptionPane.showMessageDialog(null, "No receiver has been selected");
        } else {
            this.messageClient.sendMessage(message, receivers);
        }
    }


    /**
     * @return instance of mainPanel
     */
    public MainPanel getMainPanel() {
        return panel;
    }

    /**
     * calls to update online list in left panel
     * @param onlineUsers list of online users 
     */
    public void updateOnlineList(ArrayList<String> onlineUsers){
        panel.getlPanel().updateOnlineList(onlineUsers);
    }

    /**
     * calls to update contacts list in right panel
     * @param contactsList list of contacts
     */
    public void updateContactsList(ArrayList<String> contactsList) {
        panel.getrPanel().populateRPanel(contactsList); //show contacts on rPanel
        panel.getlPanel().updateContactsList(contactsList); //show contacts on lPanel for selection
    }

    /**
     * calls to update contacts list for user that is logged in.
     */
    public void addToContacts(String userToAdd) {
        messageClient.addToContacts(userToAdd);
    }

    /**
     * sends message further to center panel.
     * @param receivedMessage message received from another user.
     */
    public void tempStoreMessage(Message receivedMessage, boolean groupChat) {
        panel.getcPanel().tempStoreMessage(receivedMessage, groupChat);
    }

    /**
     * sends message further to center panel.
     * @param sendingMessage message sent by current user.
     */
    public void tempStoreOwnMessage(Message sendingMessage) {
        panel.getcPanel().tempStoreOwnMessage(sendingMessage);
    }

    /**
     * calls to log out user in MessageClient.
     */
    public void logOut() {
        messageClient.logOut();
    }

    /**
     * sets notification when user receives a message.
     * @param notification message to be shown as notification.
     */
    public void sendNotification(String notification) {
        setTitle(notification);
    }
}
