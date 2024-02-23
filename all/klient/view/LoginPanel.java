package all.klient.view;

import com.sun.tools.javac.Main;
import all.jointEntity.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class LoginPanel extends JFrame {
    private MainFrame mainFrame;
    private JTextField usernameField;
    private JButton choosePicture;
    private JLabel userPicture;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.setSize(500, 300);
        this.setVisible(true);
    }

    public void setUpWindow() {
        setLayout(new BorderLayout());
        this.setBackground(new Color(23, 95, 95));
        setTitle("Login");

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
        userPicture.setPreferredSize(new Dimension(150, 150));
        userPicture.setBorder(BorderFactory.createLineBorder(Color.BLACK));

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

        //action listener for when choose picture button is clicked
        choosePicture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    ImageIcon icon = new ImageIcon(selectedFile.getPath());

                    loadImage(selectedFile);
                    userPicture.setIcon(icon);
                }
            }
        });

        //logic for what happens when user clicks login
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();

                //check if image and username have been set
                if (!username.isEmpty() && userPicture.getIcon() != null) {
                    System.out.println("Username: " + username); //test print
                    dispose(); //close window
                } else if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No username provided. Please try again.");
                } else if (userPicture.getIcon() == null) {
                    JOptionPane.showMessageDialog(null, "No picture chosen. Please try again.");
                }

                mainFrame.getMainPanel().getrPanel().setCurrentUsername(username);
                mainFrame.getMainPanel().getrPanel().setImage(new ImageIcon("all/user_images/" + usernameField.getText() + ".png"));
                //TODO: save username & picture (in file?)
                //TODO: how to get info to main panel after this. send it to server from client?
            }
        });
    }

    private void loadImage(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            saveImageToFile(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveImageToFile(BufferedImage image) {
        try {
            String path = "all/user_images/" + usernameField.getText() + ".png";
            File outputfile = new File(path);
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
