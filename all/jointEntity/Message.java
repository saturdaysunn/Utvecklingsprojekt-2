package all.jointEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Serializable {

    private User sender;
    private ArrayList<User> receiverList;
    private String text;
    private Date receivedTime;
    private Date deliveredTime;

    public Message(User sender, ArrayList<User> receiverList, String text,
                   Date receivedTime, Date deliveredTime){

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

    public Date getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
    }

    public Date getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(Date deliveredTime) {
        this.deliveredTime = deliveredTime;
    }
}