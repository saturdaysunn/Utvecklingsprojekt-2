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
    private JButton [][] buttonArray = new JButton[2][2];


    public LoginPanel(int width, int height, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setVisible(true);
        this.setBackground(new Color(0, 0, 0));
        //setUpWindow();
    }

    public void setUpWindow(String username, Icon icon) {

        button = new JButton(username, icon);
        for (int i = 0; i < buttonArray.length; i++) {
            for (int j = 0; j < buttonArray[i].length; j++) {
                if(buttonArray[i][j] == null){
                    buttonArray[i][j] = button;
                }
            }
        }

    }
}
