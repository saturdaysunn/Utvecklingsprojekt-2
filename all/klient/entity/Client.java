package all.klient.entity;

import all.jointEntity.User;
import all.klient.controller.MessageClient;

import java.util.HashMap;

public class Client {
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
