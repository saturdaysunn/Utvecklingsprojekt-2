package klient.controller;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import jointEntity.Message;
import jointEntity.User;
import klient.view.MainFrame;

public class ClientController {

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private HashMap<User, ArrayList<Message>> userArrayListHashMap;

    private MainFrame view; //???
    private OnlineUser onlineUser; //???


    public ClientController(String ip, int port) throws IOException {

        socket = new Socket(ip, port);
        ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        new ClientInput().start();

    }

    public void sendMessage(Message message) throws IOException {

        oos.writeObject(message);
        oos.flush();

    }

    private class ClientInput extends Thread {

        public void run() {

            Message message;

            try {
                while (true) {
                    message = (Message) ois.readObject();
                    // TODO
                    // fixa callback/pcl för att skicka meddelande till GUI
                }
            } catch (Exception e) {}

            if(socket!=null) {
                try {
                    socket.close();
                } catch (IOException e) {}
            }

            System.out.println("Klient kopplar ner");

        }

    }


    public void populateRightPanel(String[] contactsString) {
        view.populateRPanel(contactsString);

    }


    // hashmap med users och messages


}