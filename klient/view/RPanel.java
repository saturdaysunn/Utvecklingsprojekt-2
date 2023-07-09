package klient.view;

import javax.swing.*;
import java.awt.*;

public class RPanel extends JPanel {

    private int width;
    private int height;
    private MainFrame mainFrame;
    private JList<Object> rightPanelList;
    private JLabel contactsTitle;

    public RPanel(int width, int height, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.setLayout(null);
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setBounds(750, 0, 250, 600);
        this.setBackground(new Color(252, 196, 222));
        setUp();
    }

    public void setUp(){

        contactsTitle = new JLabel("Contact list:");
        contactsTitle.setLocation(50, -70);
        contactsTitle.setSize(width, height - 100);
        contactsTitle.setFont(new Font("Helvetica", Font.BOLD, 19));
        add(contactsTitle);

        rightPanelList = new JList<>();
        rightPanelList.setLocation(105, 200);
        rightPanelList.setSize(width, height - 100);
        rightPanelList.setBackground(new Color(252, 196, 222));
        rightPanelList.setFont(new Font("Helvetica", Font.PLAIN, 16));
        rightPanelList.setEnabled(false);
        add(rightPanelList);

    }

    protected void populateRPanel(String[] contactsArray){
        //there should be a toString method which makes user array to string array
        rightPanelList.setListData(contactsArray);
    }

}