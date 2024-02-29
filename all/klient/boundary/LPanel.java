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
    private List<String> selectedUsers;
    private ArrayList<String> currentUserContacts = new ArrayList<>();
    private HashMap<String, ArrayList<String>> userContacts = new HashMap<>();
    private ArrayList<String> onlineUsersList = new ArrayList<>();

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
                    mainFrame.addToContacts(selectedUser); //call mainframe to call messageclient to add to contacts var in User obj
                } else {
                    JOptionPane.showMessageDialog(LPanel.this, "Please select one user.");
                }
            }
        });


        viewChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedObjects = leftPanelList.getSelectedValuesList();
                if (selectedObjects.size() == 1) { //check so only one user has been selected
                    String selectedObject = selectedObjects.get(0);
                    if (selectedObject != null) {
                        mainFrame.getMainPanel().getcPanel().viewChat(selectedObject);
                        //TODO: implement more here? what exactly?
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
        selectedUsers = leftPanelList.getSelectedValuesList();
        return new ArrayList<>(selectedUsers);
    }


    /**
     * populates left panel with users that are online
     * @param onlineUsers list of online users
     */
    public void updateOnlineList(ArrayList<String> onlineUsers){
        onlineUsersList = onlineUsers;
        leftPanelList.setListData(onlineUsersList.toArray(String[]::new));
    }


    private class CheckboxListCellRenderer extends JCheckBox implements ListCellRenderer<String> {
        private static final long serialVersionUID = 1L;

        public CheckboxListCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list,
                                                      String value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            setText(value);
            setSelected(isSelected);
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return this;
        }
    }
}