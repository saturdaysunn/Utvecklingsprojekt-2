package all.klient.view;

import all.klient.controller.MessageClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LoginPanel extends JFrame {
    private MainFrame mainFrame;
    private JTextField usernameField;
    private JButton choosePicture;
    private JLabel userPicture;
    private MessageClient messageClient;

    //TODO: possibly change so login panel opens before mainframe
    public LoginPanel(MainFrame mainFrame, MessageClient messageClient) { //TODO: parameters would change if logic changes
        this.mainFrame = mainFrame;
        this.messageClient = messageClient;
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
                    userPicture.setIcon(resizePreviewImage(icon));
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
                } /*else if (userPicture.getIcon() == null) {
                    JOptionPane.showMessageDialog(null, "No picture chosen. Please try again.");
                }*/

                //display user's name and icon on GUI
                mainFrame.getMainPanel().getrPanel().setCurrentUsername(username);
                mainFrame.getMainPanel().getrPanel().setImage(new ImageIcon("all/user_images/" + usernameField.getText() + ".png"));

                loginUser(username);
            }
        });
    }


    //TODO: here it would be beneficial to use the online message Johan was talking about.
    /**
     * in charge of forwarding message to client to inform sender a user has logged in
     */
    public void loginUser(String username) { //TODO: unsure about parameters
        //TODO: send message to server from client that user is online
        //TODO: e.g. className.loginUser(username, messageClient);
        //observe sending of instance of messageClient. ask about this.

        //TODO: this should be done in messageClient instead and call to userController to save to file
        /*
        if (!mainFrame.checkIfUserAlreadyExists(username)) {
            mainFrame.saveUserToFile(username);
        }else {
        } */
    }

    private void loadImage(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            saveImageToFile(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * saves user's image to file for later retrieval
     * @param image
     */
    private void saveImageToFile(BufferedImage image) {
        try {
            String path = "all/user_images/" + usernameField.getText() + ".png";
            File outputfile = new File(path);
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * resizes image to fit in window
     * @param originalIcon image icon to be resized
     * @return resized image icon
     */
    public static ImageIcon resizePreviewImage(ImageIcon originalIcon) {
        Image originalImage = originalIcon.getImage();
        int width = 140; //specify the desired width and height
        int height = 140;
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
}
