package all.klient;

import all.jointEntity.Message;
import all.jointEntity.User;

import java.util.ArrayList;

public interface CallBackInterface {

     void newMessage(Message message);

     void newLogin(ArrayList<String> onlineUsers);

}
