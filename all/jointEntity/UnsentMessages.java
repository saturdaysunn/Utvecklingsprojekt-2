package all.jointEntity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * allows unsent messages to be sent as object
 */
public class UnsentMessages implements Serializable {
    private ArrayList<Message> unsentList;

    public UnsentMessages (ArrayList<Message> unsentList) {
        this.unsentList = unsentList;
    }

    public synchronized void add (Message message) {
        unsentList.add(message);
    }

    public ArrayList<Message> getUnsentList() {
        return unsentList;
    }





}
