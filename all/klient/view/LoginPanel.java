package all.klient.view;

import com.sun.tools.javac.Main;
import all.jointEntity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class LoginPanel extends JFrame {
    private MainFrame mainFrame;
    private JTextField usernameField;
    private JButton choosePicture;
    private JLabel userPicture;


    public LoginPanel(int width, int height, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.setSize(width, height);
        this.setVisible(true);
    }

    public void setUpWindow() {
        setLayout(new BorderLayout());
        this.setBackground(new Color(23, 95, 95));
        //username panel
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout());
        JLabel usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField(20);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        //picture panel
        JPanel picturePanel = new JPanel();
        picturePanel.setLayout(new FlowLayout());
        choosePicture = new JButton("Choose Picture");
        userPicture = new JLabel();
        userPicture.setPreferredSize(new Dimension(100, 100));
        picturePanel.add(choosePicture);
        picturePanel.add(userPicture);

        //login button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton loginButton = new JButton("Login");
        buttonPanel.add(loginButton);

        //add panels to frame
        add(usernamePanel, BorderLayout.NORTH);
        add(picturePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);


        choosePicture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    ImageIcon icon = new ImageIcon(selectedFile.getPath());
                    userPicture.setIcon(icon);
                }
            }
        });

        //logic for what happens when user clicks login
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                // You can add your login logic here
                System.out.println("Username: " + username);
            }
        });


    }
}
