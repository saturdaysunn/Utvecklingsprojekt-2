package klient.view;

import com.sun.tools.javac.Main;

import javax.swing.*;

public class LoginPanel extends JPanel {

    private int width;
    private int height;
    private MainFrame mainFrame;
    private JTextField panelText;
    private JButton userButton;


    public LoginPanel(int width, int height, MainFrame mainFrame){
        this.mainFrame = mainFrame;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setVisible(true);
    }

    public void setUpWindow(){



    }

}
