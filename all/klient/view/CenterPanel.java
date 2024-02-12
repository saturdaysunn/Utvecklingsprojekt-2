package all.klient.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public class CenterPanel extends JPanel{

    private int width;
    private int height;
    private MainFrame mainFrame;
    private Border border;
    private JTextField messageInputField;
    private JButton sendButton;

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

        //Let the panel have a border, 10 pixels on all sides
        Border margin = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        setBorder(new CompoundBorder(border, margin));

        JPanel messageInputPanel = new JPanel(new FlowLayout()); //message input box

        //Create a new JTextField for the message input box
        messageInputField = new JTextField(25); // width
        messageInputPanel.add(messageInputField);

        //Create send button
        sendButton = new JButton("Send");
        messageInputPanel.add(sendButton);

        //Add message input panel to main panel
        add(messageInputPanel, BorderLayout.SOUTH);
    }

    //TODO: Add action listener to send button
    //TODO: send to server to save messages to message array in hashmap??
    //TODO: then save to file if offline??


}