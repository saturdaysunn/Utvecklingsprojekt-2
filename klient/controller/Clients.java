package klient.controller;
import jointEntity.User;
import klient.entity.Client;

import java.util.HashMap;

public class Clients {

    private HashMap<User, Client> clients = new HashMap<User, Client>();

    public Clients(){



    }

    public void put(User user, Client client){
        synchronized (this){
            clients.put(user, client);
        }
    }

    public synchronized Client get(User user){

        return get(user);

    }

}
