package all.klient.view;

import all.klient.controller.*;
import javax.swing.*;
import all.Controller;

public class MainFrame extends JFrame {

    private JFrame frame;
    private MainPanel panel;
    private Controller controller; //do we need??
    private int width = 1500;
    private int height = 1100;

    public MainFrame(int width, int height, Controller controller){

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

    public void userLoggedIn(String userName){
        controller.userLoggedIn(userName);
    }


}

