package klient.controller;
import jointEntity.Message;
import jointEntity.User;
import klient.CallBackInterface;
import klient.entity.Client;
import klient.view.MainFrame;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The class initiates a connection between the client and the server
 *
 */

public class MessageClient {

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private HashMap<User, ArrayList<Message>> userArrayListHashMap;
    private MainFrame view; //???
    private OnlineUser onlineUser; //???
    private HashMap<User, Client> clients = new HashMap<User, Client>();
    private ArrayList<CallBackInterface> listeners = new ArrayList<>();

    public MessageClient(String ip, int port){

        try{
            socket = new Socket(ip, port);
            new Listener().start();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void addListener(CallBackInterface listener){
        listeners.add(listener);
    }


    public void put(User user, Client client){
        synchronized (this){
            clients.put(user, client);
        }
    }

    public synchronized Client get(User user){

        return get(user);

    }

    private class Listener extends Thread {

        @Override
        public void run() {

            Message message;

            try {
                ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                while(true){
                    message = (Message) ois.readObject();
                    for (CallBackInterface listener: listeners){
                        listener.newMessage(message);
                    }
                }
            } catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }

        }
    }

}
