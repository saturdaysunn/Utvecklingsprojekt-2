package all.klient.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * displays contacts. Populated from server by reading from file on harddisk
 */
public class RPanel extends JPanel {

    private int width;
    private int height;
    private MainFrame mainFrame;
    private JList<Object> rightPanelList;
    private JLabel contactsTitle;
    private JLabel currentUserLabel;
    private JLabel currentUserPictureLabel;
    private ImageIcon image = new ImageIcon("all/files/default-image.png");


    public RPanel(int width, int height, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.setLayout(new BorderLayout());
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setBounds(750, 0, 250, 600); //set to the right on the main panel
        this.setBackground(new Color(227, 195, 255)); //colour
        setUp();
    }

    public void setUp(){
        JLabel contactsLabel = new JLabel("Contacts");
        contactsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(contactsLabel, BorderLayout.NORTH);

        rightPanelList = new JList<>();
        rightPanelList.setLocation(105, 200);
        rightPanelList.setSize(width, height - 100);
        rightPanelList.setBackground(new Color(218, 218, 218));
        rightPanelList.setFont(new Font("Helvetica", Font.PLAIN, 16));
        rightPanelList.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(rightPanelList); //should be scrollable when many users online
        add(scrollPane, BorderLayout.CENTER);


        JPanel currentUserPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currentUserLabel = new JLabel();
        currentUserPictureLabel = new JLabel();
        currentUserPictureLabel.setIcon(resizeImage(image));

        currentUserPanel.setPreferredSize(new Dimension(20, 90));

        currentUserPanel.add(currentUserLabel, BorderLayout.WEST);
        currentUserPanel.add(currentUserPictureLabel, BorderLayout.EAST);

        add(currentUserPanel, BorderLayout.SOUTH);
    }

    public void setCurrentUsername(String username){
        this.currentUserLabel.setText(username);
    }

    public String getCurrentUsername() {
        return currentUserLabel.getText();
    }

    public void setUserIcon(ImageIcon image) {
        currentUserPictureLabel.setIcon(resizeImage(image));
    }

    public ImageIcon getUserIcon() {
        return (ImageIcon) currentUserPictureLabel.getIcon(); //TODO: correct?
    }


    /**
     * populates right panel with contacts
     * @param contactsList
     */
    protected void populateRPanel(List<String> contactsList){
        for(String contact : contactsList){

            System.out.println(contact);

        }

        String[] contactsArray = contactsList.toArray(String[]::new);

        rightPanelList.setListData(contactsArray);
    }

    public static ImageIcon resizeImage(ImageIcon originalIcon) {
        Image originalImage = originalIcon.getImage();
        int width = 40; // specify the desired width and height
        int height = 40;
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
}