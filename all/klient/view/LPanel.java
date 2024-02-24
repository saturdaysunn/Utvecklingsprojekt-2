package all.klient.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
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
    private JList<String> leftPanelList;
    private JButton addToContactsButton;
    private JButton viewChatButton;
    private List<String> selectedUsers;
    private ArrayList<String> currentUserContacts = new ArrayList<>();
    private HashMap<String, ArrayList<String>> userContacts = new HashMap<>();

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

        populateLPanel(readFromFile("all/files/users.txt")); //TODO: fix so updated from server, not file

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addToContactsButton = new JButton("Add to Contacts");
        viewChatButton = new JButton("View Chat");
        buttonPanel.add(addToContactsButton); //add button to panel
        buttonPanel.add(viewChatButton); //add button to panel
        buttonPanel.setPreferredSize(new Dimension(20, 100));


        addToContactsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: what if several have been selected?
                Object selectedObject = leftPanelList.getSelectedValue();

                if (selectedObject != null) {
                    //TODO: check if already in contacts
                    //TODO: logic to add to contacts file

                    String currentUser = mainFrame.getMainPanel().getrPanel().getCurrentUserLabel(); //current user
                    selectedUsers = leftPanelList.getSelectedValuesList(); //selected users on LPanel

                    for(String selectedUser : selectedUsers) {
                        if (!currentUserContacts.contains(selectedUser)) {
                            currentUserContacts.add(selectedUser);
                        }
                    }

                    userContacts.put(currentUser, currentUserContacts);
                    writeHashMapToFile(userContacts, "all/files/contacts.txt");


                } else {
                    JOptionPane.showMessageDialog(LPanel.this, "Please select a user.");
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
                    }
                } else {
                    JOptionPane.showMessageDialog(buttonPanel, "Please select only one user to view chat.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(buttonPanel, BorderLayout.SOUTH); //add to south of panel
    }

    /**
     * populates left panel with online users
     * @param contactsList
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

    public static void writeHashMapToFile(HashMap<String, ArrayList<String>> hashMap, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            for (Map.Entry<String, ArrayList<String>> entry : hashMap.entrySet()) {
                String key = entry.getKey();
                ArrayList<String> values = entry.getValue();

                writer.write(key);
                writer.newLine();

                for (String value : values) {
                    writer.write(value);
                    writer.newLine();
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public LinkedList<String> readFromFile(String filePath) {
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