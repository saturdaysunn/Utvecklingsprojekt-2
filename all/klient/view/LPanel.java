package all.klient.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * diaplays online users. Updated every time a user logs in or out
 */
public class LPanel extends JPanel{
    private int width;
    private int height;
    private MainFrame mainFrame;
    private JList<Object> leftPanelList;
    private JButton addToContactsButton;

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

        String[] onlineArray = {"user1", "user2", "user3"}; //TODO: test values
        populateLPanel(onlineArray);

        JPanel addContactPanel = new JPanel(new FlowLayout());
        addToContactsButton = new JButton("Add to Contacts");
        addContactPanel.add(addToContactsButton); //add button to panel
        addContactPanel.setPreferredSize(new Dimension(20, 70));
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
    protected void populateLPanel(String[] onlineArray){
        //there should be a toString method which makes user array to string array
        leftPanelList.setListData(onlineArray);
    }

}