package klient.view;

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
        this.setLayout(null);
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setVisible(true);
        this.setBounds(200, 0, 250, 600);
        this.setBackground(new Color(255, 255, 255));
        setUp();
    }

    public void setUp(){

        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        border = this.getBorder();

        //Let the panel have a border, 10 pixels on all sides
        Border margin = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        setBorder(new CompoundBorder(border, margin));

        JPanel messageInputPanel = new JPanel(new BorderLayout());
        //JPanel messageInputPanel = new JPanel();

        // Create a new JTextField for the message input box
        messageInputField = new JTextField();
        messageInputPanel.add(messageInputField, BorderLayout.EAST);
        //messageInputPanel.setLocation(250, 40);

        // Create a new JButton for the send button
        sendButton = new JButton("Send");
        messageInputPanel.add(sendButton, BorderLayout.EAST);
        //messageInputPanel.add(sendButton);
        //sendButton.setLocation(320, 40);

        // Add the message input panel to the main panel
        add(messageInputPanel, layout.SOUTH);

    }


}