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
        GridLayout layout = new GridLayout(2,2);
        setLayout(layout);
    }

    public void setUpWindow(String username, Icon icon) {

        //button = new JButton(username, icon);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if(buttonArray[i][j] == null){
                    buttonArray[i][j] = new JButton(username, icon);
                    buttonArray[i][j].setBackground(new Color(252, 196, 222));
                    buttonArray[i][j].setVisible(true);
                    buttonArray[i][j].setOpaque(true);
                    buttonArray[i][j].setSize(new Dimension(50, 50));
                    add(buttonArray[i][j]);
                    break;
                }
            }
        }

    }
}
