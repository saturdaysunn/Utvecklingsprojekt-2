package all.klient.boundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * displays online users. Updated every time a user logs in or out
 */
public class LPanel extends JPanel{
    private int width;
    private int height;
    private MainFrame mainFrame;
    private JList<String> leftPanelList;
    private JButton addToContactsButton;
    private JButton viewChatButton;
    private ArrayList<String> currentUserContacts = new ArrayList<>();
    private HashMap<String, ArrayList<String>> userContacts = new HashMap<>();
    private ArrayList<String> onlineUsersList = new ArrayList<>();
    private ArrayList<String> contactsList = new ArrayList<>();

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

        leftPanelList = new JList<String>();
        leftPanelList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        leftPanelList.setLocation(0, 200);
        leftPanelList.setSize(width, height - 100);
        leftPanelList.setBackground(new Color(215, 215, 215));
        leftPanelList.setFont(new Font("Helvetica", Font.PLAIN, 16));
        leftPanelList.setEnabled(true); //controls if you can select items in the list

        leftPanelList.setCellRenderer(new CheckboxListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(leftPanelList); //should be scrollable when many users online
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addToContactsButton = new JButton("Add to Contacts");
        viewChatButton = new JButton("View Chat");
        buttonPanel.add(addToContactsButton); //add button to panel
        buttonPanel.add(viewChatButton); //add button to panel
        buttonPanel.setPreferredSize(new Dimension(20, 100));


        addToContactsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = leftPanelList.getSelectedValue();
                System.out.println("selected user: " + selectedUser); //test

                if (selectedUser != null) {
                    selectedUser = selectedUser.replaceAll("\\<.*?\\>", "");
                    mainFrame.addToContacts(selectedUser); //call mainframe to call messageclient to add to contacts var in User obj
                } else {
                    JOptionPane.showMessageDialog(LPanel.this, "Please select one user.");
                }
            }
        });


        viewChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("view chat button as been called");
                List<String> selectedObjects = leftPanelList.getSelectedValuesList();

                if (selectedObjects.size() == 1) { //check so only one user has been selected
                    String selectedObject = selectedObjects.get(0);
                    if (selectedObject != null) {
                        String plainName = selectedObject.replaceAll("\\<.*?\\>", "");
                        mainFrame.getMainPanel().getcPanel().viewChat(plainName);
                    }
                } else {
                    JOptionPane.showMessageDialog(buttonPanel, "Please select only one user to view chat.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(buttonPanel, BorderLayout.SOUTH); //add to south of panel
    }

    /**
     * @return user(s) selected from lPanel
     */
    public ArrayList<String> getReceivers() {
        ArrayList<String> selectedUsers = new ArrayList<>();
        List<String> selectedValues = leftPanelList.getSelectedValuesList();
        for (String person : selectedValues) {
            String plainName = person.replaceAll("\\<.*?\\>", "");
            selectedUsers.add(plainName);
            System.out.println("receiver is: " + plainName);
        }
        System.out.println("the selected users are: " + selectedUsers);
        return selectedUsers;
    }


    /**
     * populates left panel with users that are online
     * @param onlineUsers list of online users
     */
    public void updateOnlineList(ArrayList<String> onlineUsers){
        this.onlineUsersList = onlineUsers;
        populateLPanel();
    }

    //TODO: use this and the online list together to show all in LPanel, online users in green text.
    public void updateContactsList(ArrayList<String> contactsList) {
        this.contactsList = contactsList;
        populateLPanel();
    }

    public void populateLPanel() {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.clear();

        // Add online users
        if (onlineUsersList != null) {
            for (String user : onlineUsersList) {
                listModel.addElement("<html><font color='green'>" + user + "</font></html>");
            }
        }

        // Add offline users
        if (contactsList != null) {
            for (String user : contactsList) {
                if (!onlineUsersList.contains(user)) {
                    listModel.addElement(user);
                }
            }
        }

        leftPanelList.setModel(listModel);
    }

    private class CheckboxListCellRenderer extends JCheckBox implements ListCellRenderer<String> {
        private static final long serialVersionUID = 1L;

        public CheckboxListCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setText(value);
            setSelected(isSelected);
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return this;
        }
    }
}