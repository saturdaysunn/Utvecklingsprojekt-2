package all.klient.entity;

import all.jointEntity.User;
import all.klient.controller.MessageClient;

import java.util.HashMap;

public class Client {
    //TODO: don't think we need this class
    private HashMap<User, MessageClient> clients = new HashMap<User, MessageClient>();

    /**
     * Adds user and related connection to hashmap
     * @param user user
     * @param client connection
     */
    public void addUser(User user, MessageClient client){
        synchronized (this){
            clients.put(user, client);
        }
    }


}
