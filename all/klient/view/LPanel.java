package all.klient.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

//TODO: change so that the panel is called only users
//TODO: online users have green circle
//TODO: offline users have grey circle
//TODO: such that any can receive a message


/**
 * diaplays online users. Updated every time a user logs in or out
 */
public class LPanel extends JPanel{
    private int width;
    private int height;
    private MainFrame mainFrame;
    private JList<Object> leftPanelList;
    private JButton addToContactsButton;
    private ArrayList<String> selectedUsers = new ArrayList<>();

    public LPanel(int width, int height, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.width = width;
        this.height = height;
        setLayout(new BorderLayout());
        this.setSize(width, height);
        this.setBounds(0, 0, 250, 600);
        this.setBackground(new Color(199, 248, 241));
        setUp();
    }

    public void setUp(){
        JLabel onlineUsersLabel = new JLabel("Online Users");
        onlineUsersLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(onlineUsersLabel, BorderLayout.NORTH);

        leftPanelList = new JList<>();
        leftPanelList.setLocation(0, 200);
        leftPanelList.setSize(width, height - 100);
        leftPanelList.setBackground(new Color(215, 215, 215));
        leftPanelList.setFont(new Font("Helvetica", Font.PLAIN, 16));
        leftPanelList.setEnabled(true); //controls if you can select items in the list
        JScrollPane scrollPane = new JScrollPane(leftPanelList); //should be scrollable when many users online
        add(scrollPane, BorderLayout.CENTER);

        List<String> contactsList = readFromFile("all/files/users.txt");
        populateLPanel(contactsList);

        JPanel addContactPanel = new JPanel(new FlowLayout());
        addToContactsButton = new JButton("Add to Contacts");
        addContactPanel.add(addToContactsButton); //add button to panel
        addContactPanel.setPreferredSize(new Dimension(20, 90));
        addToContactsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selectedObject = leftPanelList.getSelectedValue();
                if (selectedObject != null) {
                    //TODO: check if already in contacts
                    //TODO: logic to add to contacts file
                    JOptionPane.showMessageDialog(LPanel.this, "Added " + selectedObject.toString() + " to contacts.");
                } else {
                    JOptionPane.showMessageDialog(LPanel.this, "Please select a user.");
                }
            }
        });
        add(addContactPanel, BorderLayout.SOUTH); //add to south of panel



    }

    /**
     * populates left panel with online users
     * @param onlineArray
     */
    protected void populateLPanel(List<String> contactsList){

        String[] contactsArray = contactsList.toArray(String[]::new);

        leftPanelList.setListData(contactsArray);
    }

    //TODO: implement method which reads which users have been selected
    //TODO: call method to reset buttons (should have a check mark next to them) and empty arraylist of selectedUsers
    //TODO: the latter is to be called from mainFrame.

    public String getUsername() {
        //TODO: returns username from JLabel above users list
        return null; //TODO: temp
    }

    public ArrayList<String> getReceivers() {
        //TODO: returns users which have been selected
        return null; //TODO: temp
    }

    public static LinkedList<String> readFromFile(String filePath) {
        LinkedList<String> lines = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }
}