package all.jointEntity;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class User implements Serializable {
    private String username;
    private ImageIcon icon;
    private ArrayList<String> contacts = new ArrayList<>();

    public User(String username, ImageIcon icon){
        this.username = username;
        this.icon = icon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof User) {
            return username.equals(((User) obj).getUsername());
        } return false;
    }

    public void addContact(String user){
        contacts.add(user);
    }

    public ArrayList<String> getContacts(){
        return contacts;
    }


}
