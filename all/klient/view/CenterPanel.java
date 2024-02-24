package all.klient.view;

import all.jointEntity.ImageMessage;
import all.jointEntity.Message;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class CenterPanel extends JPanel{
    private int width;
    private int height;
    private MainFrame mainFrame;
    private Border border;
    private JTextField messageInputField;
    private JButton sendButton;
    private JButton uploadImageButton;

    public CenterPanel(int width, int height, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setVisible(true);
        this.setBounds(250, 0, 500, 580);
        this.setBackground(new Color(238, 95, 95));
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
                System.out.println("send button pressed"); //test
                sendMessage(messageInputField.getText()); //send contents of input field as message
                //TODO: either parameter can be null
                messageInputField.setText(""); //clear input field after sending message
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

                    //TODO: fix how to make sure image gets sent/saved to server
                } else {
                    System.out.println("No file chosen");
                }
            }
        });

    }

    public void sendMessage(String message) {
        mainFrame.sendMessage(message);
    }


    /**
     * displays chat with a given user
     * @param userName name of selected user
     */
    public void viewChat(String userName) {
        System.out.println("i have been called to view chat");
        //TODO: communicate to mainframe and then to controller to retrieve messages for chat with given user.
        //TODO: send messages to populate chat window
        populateChatWindow();
    }

    /**
     * populates chat window with messages from selected user
     */
    public void populateChatWindow() {
        //populate chat window with messages (text and images, somehow).
    }

}