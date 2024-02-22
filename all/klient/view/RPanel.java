package all.klient.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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

        String[] contactsArray = {"user1", "user2", "user3"}; //TODO: test values, to be replaced with reading from file
        populateRPanel(contactsArray);


        JPanel currentUserPanel = new JPanel();
        currentUserLabel = new JLabel();
        currentUserPanel.setPreferredSize(new Dimension(20, 70));
        currentUserPanel.add(currentUserLabel); //add button to panel
        add(currentUserPanel, BorderLayout.SOUTH);

    }

    public void setCurrentUsername(String username){

        this.currentUserLabel.setText(username);

    }

    /**
     * populates right panel with contacts
     * @param contactsArray
     */
    protected void populateRPanel(String[] contactsArray){
        //there should be a toString method which makes user array to string array
        rightPanelList.setListData(contactsArray);
    }


}