package all.jointEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Serializable {

    private User sender;
    private ArrayList<User> receiverList;
    private String text;
    private String receivedTime;
    private String deliveredTime;

    public Message(User sender, ArrayList<User> receiverList, String text,
                   String receivedTime, String deliveredTime){

        this.sender = sender;
        this.receiverList = receiverList;
        this.text = text;
        this.receivedTime = receivedTime;
        this.deliveredTime = deliveredTime;

    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public ArrayList<User> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(ArrayList<User> receiverList) {
        this.receiverList = receiverList;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }

    public String getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(String deliveredTime) {
        this.deliveredTime = deliveredTime;
    }
}