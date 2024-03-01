package all.klient.boundary;

import all.jointEntity.ImageMessage;
import all.jointEntity.Message;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

public class CenterPanel extends JPanel{
    private int width;
    private int height;
    private MainFrame mainFrame;
    private Border border;
    private JTextField messageInputField;
    private JButton sendButton;
    private JButton uploadImageButton;
    private JLabel userChatLabel;
    private JPanel chatNamePanel;
    private HashMap<String, ArrayList<Message>> conversationMap; //stores all conversation history.

    public CenterPanel(int width, int height, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setVisible(true);
        this.setBounds(250, 0, 500, 580);
        this.setBackground(new Color(238, 95, 95));
        this.conversationMap = new HashMap<>(); //initiate
        setUp();
    }

    /**
     * sets up center panel containing messages and message input box
     */
    public void setUp() {
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        border = this.getBorder();
        Border margin = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        setBorder(new CompoundBorder(border, margin));

        chatNamePanel = new JPanel(new BorderLayout()); //initialize
        userChatLabel = new JLabel(); //initialize
        chatNamePanel.add(userChatLabel, BorderLayout.CENTER);
        chatNamePanel.setPreferredSize(new Dimension(500, 25));
        add(chatNamePanel, BorderLayout.NORTH); //add to center panel
        setChatName(null);

        //chat area to display messages
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false); //non-editable
        JScrollPane scrollableChatPane = new JScrollPane(chatArea); //scroll bar for many messages
        add(scrollableChatPane, BorderLayout.CENTER); //add chat window to the center

        JPanel messageInputPanel = new JPanel(); //panel for input box
        messageInputPanel.setLayout(new BoxLayout(messageInputPanel, BoxLayout.X_AXIS));
        messageInputField = new JTextField(25);

        JScrollPane scrollableInputField = new JScrollPane(messageInputField); //TODO: scroll doesn't work right now. FIX
        messageInputPanel.add(scrollableInputField);

        sendButton = new JButton("Send"); //button to send message
        uploadImageButton = new JButton("Upload Image"); //button to upload image

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(sendButton);
        buttonPanel.add(uploadImageButton);
        messageInputPanel.add(buttonPanel);
        add(messageInputPanel, BorderLayout.SOUTH);

        /**
         * listens to send button to register message to be sent
         */
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("send button pressed");
                if (!messageInputField.getText().isEmpty()) { //if no message to send
                    sendMessage(messageInputField.getText()); //send contents of input field as message
                    messageInputField.setText(""); //clear input field after sending message
                } else {
                    JOptionPane.showMessageDialog(null, "Please input a message first");
                }
            }
        });

        /**
         * listens to upload image button to register image to be sent
         */
        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("upload image button pressed"); //test
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile(); //retrieves path in computer
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());

                    String existingText = messageInputField.getText();
                    if (!existingText.isEmpty()) { //if already text in message input box
                        existingText += " ";
                    }
                    existingText += selectedFile.getAbsolutePath();
                    messageInputField.setText(existingText); //update text in input field with image
                } else {
                    System.out.println("No file chosen");
                }
            }
        });

    }

    /**
     * forwards message to be processed in controller class
     * @param message message in string format
     */
    public void sendMessage(String message) {
        mainFrame.sendMessage(message);
    }


    /**
     * displays chat with a given user
     * @param userName name of selected user
     */
    public void viewChat(String userName) {
        System.out.println("i have been called to view chat for " + userName);
        setChatName(userName); //sets name of user chatting with
        populateChatWindow(userName);
    }

    /**
     * populates chat window with messages from selected user
     */
    public void populateChatWindow(String selectedUserName) {
        if (conversationMap.containsKey(selectedUserName)) {
            ArrayList<Message> conversation = conversationMap.get(selectedUserName);
            for (Message message : conversation) { //for each message in conversation history
                //TODO: populate chat window with messages (text and images).
                if (message instanceof ImageMessage) {
                    System.out.println("image message to go on chat view");
                    ImageMessage imageMessage = (ImageMessage) message;
                    JLabel messageLabel = new JLabel();
                    if (imageMessage.getImage() != null) {
                        ImageIcon imageIcon = imageMessage.getImage();
                        messageLabel.setIcon(imageIcon);
                    }
                    if (imageMessage.getText() != null && !imageMessage.getText().isEmpty()) {
                        messageLabel.setText(messageLabel.getText() + " " + imageMessage.getText());
                    }
                    add(messageLabel);
                }
            }
        } else {
            System.out.println("no history contents here");
        }
    }

    /**
     * displays who user is chatting with in panel above chat window
     */
    public void setChatName(String userName) {
        if (userName == null) {
            userChatLabel.setText("No Chat Selected");
            System.out.println("no chat selected");
        } else {
            userChatLabel.setText("Showing Chat With " + userName);
            System.out.println("chat with " + userName);
        }
    }

    /**
     * receives message sent from MessageClient and stores in conversationMap containing message history.
     * @param receivedMessage instance of Message
     */
    public synchronized void newMessage(Message receivedMessage) {
        System.out.println("adding message to history map");
        String senderName = receivedMessage.getSender().getUsername();
        if (conversationMap.containsKey(senderName)) {
            ArrayList<Message> history = conversationMap.get(senderName);
            history.add(receivedMessage);
            conversationMap.put(senderName, history);
        } else {
            ArrayList<Message> history = new ArrayList<>();
            history.add(receivedMessage);
            conversationMap.put(senderName, history);
        }
    }

}