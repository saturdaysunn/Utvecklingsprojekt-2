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

        populateLPanel(mainFrame.retrieveAllUsersFromFile("all/files/users.txt"));


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

                    String currentUser = mainFrame.getMainPanel().getrPanel().getCurrentUsername(); //current user
                    selectedUsers = leftPanelList.getSelectedValuesList(); //selected users on LPanel
                    currentUserContacts = (ArrayList<String>) mainFrame.getContactsOfUser("all/files/contacts.txt", currentUser); //current user's contacts'

                    for(String selectedUser : selectedUsers) {
                        if (!currentUserContacts.contains(selectedUser)) { //check if current user's contacts contains the selected user
                            //if the user isn't already in contact, then the user is added.
                            currentUserContacts.add(selectedUser);
                        }
                    }

                    userContacts.put(currentUser, currentUserContacts); //before writing to file, the user's contacts are stored a HashMap
                    try {
                        mainFrame.removeDataBlock("all/files/contacts.txt", currentUser, "all/files/new-contacts.txt");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    mainFrame.writeHashMapToFile(userContacts, "all/files/new-contacts.txt");
                    mainFrame.populateRPanel(mainFrame.getContactsOfUser("all/files/new-contacts.txt", currentUser));



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
     * @param contactsList list
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
        try (BufferedReader reader = new BufferedReader(new FileReader(filename));
             BufferedWriter writer = new BufferedWriter(new FileWriter("all/files/temp.txt"))) {

            boolean foundKey = false;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (foundKey) {
                        //found an empty row after the key, break loop
                        break;
                    }
                    continue;
                }

                if (!foundKey) {
                    if (hashMap.containsKey(line)) {
                        foundKey = true;
                    }
                }

                //if key found, ignore lines until an empty row is found
                if (foundKey) {
                    continue;
                }

                //copy the line to the new file
                writer.write(line);
                writer.newLine();
            }

            //write the HashMap to the new fi
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

            //copy the remaining content of the file
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //rename the temporary file to the original filename
        File tempFile = new File("all/files/temp.txt");
        File originalFile = new File(filename);
        if (tempFile.exists()) {
            if (originalFile.exists())
                originalFile.delete();
            tempFile.renameTo(originalFile);
        }
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