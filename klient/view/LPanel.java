package klient.view;

import javax.swing.*;
import java.awt.*;

public class LPanel extends JPanel{

    private int width;
    private int height;
    private MainFrame mainFrame;
    private JList<Object> leftPanelList;
    private JLabel onlineTitle;

    public LPanel(int width, int height, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.setLayout(null);
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setBounds(0, 0, 250, 600);
        this.setVisible(true);
        this.setBackground(new Color(65, 233, 210));
        setUp();
    }

    public void setUp(){

        onlineTitle = new JLabel("Online list:");
        onlineTitle.setLocation(10, -70);
        onlineTitle.setSize(width, height - 100);
        onlineTitle.setFont(new Font("Helvetica", Font.BOLD, 19));
        onlineTitle.setVisible(true);
        add(onlineTitle);

        leftPanelList = new JList<>();
        leftPanelList.setLocation(0, 200);
        leftPanelList.setSize(width, height - 100);
        leftPanelList.setBackground(new Color(252, 196, 222));
        leftPanelList.setFont(new Font("Helvetica", Font.PLAIN, 16));
        leftPanelList.setEnabled(false);
        add(leftPanelList);

    }


    protected void populateLPanel(String[] onlineArray){
        //there should be a toString method which makes user array to string array
        leftPanelList.setListData(onlineArray);
    }
}