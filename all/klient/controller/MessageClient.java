package all.klient.controller;
import all.jointEntity.Message;
import all.jointEntity.User;
import all.klient.CallBackInterface;
import all.klient.entity.Client;
import all.klient.view.MainFrame;

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

public class MessageClient extends Thread {

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private HashMap<User, ArrayList<Message>> userArrayListHashMap;
    private MainFrame view; //???
    private OnlineUser onlineUser; //???
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
