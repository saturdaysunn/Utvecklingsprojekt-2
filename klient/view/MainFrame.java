package klient.view;

import klient.controller.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private JFrame frame;
    private MainPanel panel;
    private ClientController controller; //do we need??
    private int width = 1500;
    private int height = 1100;

    public MainFrame(int width, int height){

        super("Chatt");
        this.setResizable(false);
        this.setSize(width, height);
        this.panel = new MainPanel(width, height, this);
        this.setContentPane(panel);
        this.setVisible(true);
        this.setBounds(100, 100, width, height);
        this.setDefaultCloseOperation(3);

    }

    public void populateRPanel(String[] contactsArray) {

        panel.getrPanel().populateRPanel(contactsArray);

    }

    public void populateLPanel (String[] onlineArray) {
        panel.getlPanel().populateLPanel(onlineArray);
    }
}
