package jointEntity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class UnsentMessages {

    private HashMap<User, ArrayList<Message>> unsentMessages = new HashMap<User, ArrayList<Message>>();

    public UnsentMessages(){}

    public void put(User user, Message message){
        synchronized (this){
            // hämta arraylist (skapa ny om null och placera i unsentMessages)
            // lägga till message i arraylist
        }

    }

    public synchronized ArrayList<Message> get(User user){

        return unsentMessages.get(user);

    }

}
