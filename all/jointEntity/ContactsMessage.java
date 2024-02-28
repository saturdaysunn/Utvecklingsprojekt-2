package all.jointEntity;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactsMessage implements Serializable {
    private ArrayList<String> contactsList;

    public ContactsMessage(ArrayList<String> contactsList) {
        this.contactsList = contactsList;
    }

    public ArrayList<String> getContactsList() {
        return contactsList;
    }

    public void setContactsList(ArrayList<String> contactsList) {
        this.contactsList = contactsList;
    }
}
