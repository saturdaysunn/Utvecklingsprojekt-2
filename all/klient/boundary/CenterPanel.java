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
    private MainFrame mainFrame;
    private Border border;
    private JTextField messageInputField;
    private JButton sendButton;
    private JButton uploadImageButton;
    private JLabel userChatLabel;
    private JPanel chatNamePanel;
    private DefaultListModel listModel;
    private JList chatList;
    private HashMap<String, ArrayList<Message>> conversationMap; //stores all conversation history.

    public CenterPanel(int width, int height, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.setSize(width, height);
        this.setVisible(true);
        this.setBounds(150, 0, 400, 480);
        this.setBackground(new Color(245, 151, 194));
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

        listModel = new DefaultListModel<>();
        chatList = new JList<>(listModel);
        chatList.setCellRenderer(new MessageListRenderer());
        JScrollPane scrollableChatPane = new JScrollPane(chatList); //scroll bar for many messages
        add(scrollableChatPane, BorderLayout.CENTER);

        JPanel messageInputPanel = new JPanel(); //panel for input box
        messageInputPanel.setLayout(new BoxLayout(messageInputPanel, BoxLayout.X_AXIS));
        messageInputField = new JTextField(25);

        JScrollPane scrollableInputField = new JScrollPane(messageInputField); //scroll bar for long messages
        messageInputPanel.add(scrollableInputField);

        sendButton = new JButton("Send"); //button to send message
        uploadImageButton = new JButton("Upload Image"); //button to upload image

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(sendButton);
        buttonPanel.add(uploadImageButton);
        messageInputPanel.add(buttonPanel);
        add(messageInputPanel, BorderLayout.SOUTH);

        messageInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });

        /**
         * listens to send button to register message to be sent
         */
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!messageInputField.getText().isEmpty()) { //if no message to send
                    sendMessage(messageInputField.getText()); //send contents of input field
                    messageInputField.setText(""); //clear input field
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
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile(); //retrieves path in computer

                    String existingText = messageInputField.getText();
                    if (!existingText.isEmpty()) { //if already text in message input box
                        existingText += " ";
                    }
                    existingText += selectedFile.getAbsolutePath();
                    messageInputField.setText(existingText); //update text in input field with image
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
        setChatName(userName); //sets name of user chatting with
        populateChatWindow(userName);
    }

    /**
     * populates chat window with messages from selected user
     */
    public synchronized void populateChatWindow(String selectedUserName) {
        listModel.clear(); //reset
        if (conversationMap.containsKey(selectedUserName)) { //if history exists
            ArrayList<Message> conversation = conversationMap.get(selectedUserName); //retrieve
            for (Message message : conversation) { //iterate messages
                listModel.addElement(message);
            }
        }
    }

    /**
     * displays who user is chatting with in panel above chat window
     */
    public void setChatName(String userName) {
        if (userName == null) {
            userChatLabel.setText("No Chat Selected");
        } else {
            userChatLabel.setText("Showing Chat With " + userName);
        }
    }

    /**
     * stores message sent from another user in conversation history map.
     * stored under key = sender of message.
     * @param receivedMessage instance of Message
     */
    public synchronized void tempStoreMessage(Message receivedMessage, boolean groupChat) {
        String senderName = receivedMessage.getSender().getUsername();
        if (!groupChat) {
            if (conversationMap.containsKey(senderName)) {
                ArrayList<Message> history = conversationMap.get(senderName);
                history.add(receivedMessage);
                conversationMap.put(senderName, history);
            } else {
                ArrayList<Message> history = new ArrayList<>();
                history.add(receivedMessage);
                conversationMap.put(senderName, history);
            }

            //if already viewing chat with this person, populate chat window
            if (userChatLabel != null && userChatLabel.getText().equals("Showing Chat With " + senderName)) {
                populateChatWindow(senderName);
            }
        } else {
            if (conversationMap.containsKey("GroupChat")) {
                ArrayList<Message> history = conversationMap.get("GroupChat");
                history.add(receivedMessage);
                conversationMap.put("GroupChat", history);
            } else {
                ArrayList<Message> history = new ArrayList<>();
                history.add(receivedMessage);
                conversationMap.put("GroupChat", history);
            }

            //if already viewing chat with this person, populate chat window
            if (userChatLabel != null && userChatLabel.getText().equals("Showing Chat With GroupChat")) {
                populateChatWindow("GroupChat");
            }

        }
    }

    /**
     * stores message from current user in conversationHistory map
     * stored under key = receiver.
     * such that it can be viewed when opening chat with a selected user.
     * @param sendingMessage instance of message.
     */
    public synchronized void tempStoreOwnMessage(Message sendingMessage) {
        ArrayList<String> receivers = sendingMessage.getReceiverList();
        for (String receiver : receivers) {
            if (conversationMap.containsKey(receiver)) { //if there is a chat history
                ArrayList<Message> messageHistory = conversationMap.get(receiver); //retrieve from hashmap
                messageHistory.add(sendingMessage); //add new message
                conversationMap.put(receiver, messageHistory); //update
            } else {
                ArrayList<Message> messageHistory = new ArrayList<>(); //create new history
                messageHistory.add(sendingMessage); //add new message
                conversationMap.put(receiver, messageHistory); //update hashmap
            }
            //if chat with this receiver is already open, populate chat window
            if (userChatLabel != null && userChatLabel.getText().equals("Showing Chat With " + receiver)) {
                populateChatWindow(receiver);
            }
        }
    }

    private class MessageListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            Message message = (Message) value;
            String displayText = message.getSender().getUsername() + ": ";
            if (message instanceof ImageMessage) {
                displayText += message.getText();
                displayText += "[Image]";
                ImageIcon imageIcon = ((ImageMessage) message).getImage(); //to be able to display image
                setIcon(imageIcon);
            } else {
                displayText += message.getText();
                setIcon(null);
            }
            setText(displayText);
            return this;
        }
    }

}