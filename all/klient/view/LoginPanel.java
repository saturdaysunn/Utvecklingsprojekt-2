package all.klient.view;

import com.sun.tools.javac.Main;
import all.jointEntity.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LoginPanel extends JPanel {

    private int width;
    private int height;
    private MainFrame mainFrame;
    private JTextField panelText;
    private JButton button;
    private JList<Object> loginPanelList;


    public LoginPanel(int width, int height, MainFrame mainFrame){
        this.mainFrame = mainFrame;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setVisible(true);
        this.setBackground(new Color(0, 0, 0));
        //setUpWindow();
    }

    public void setUpWindow(ArrayList<User> users) {


        for (User user : users) {
            button = new JButton(user.getUsername(), user.getIcon());
            button.addActionListener(L -> mainFrame.userLoggedIn(button.getText()));
            this.add(button);
        }

    }

}
