package all.server;

import all.jointEntity.Message;
import all.jointEntity.User;
import all.klient.controller.FileController;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Server {
    private Connection connection;
    private HashMap<User, ClientHandler> onlineClients = new HashMap<>(); //stores clients and their connections
    private HashMap<String, ArrayList<Message>> unsentMessagesMap; //Key: receiver, Value: unsent messages arraylist
    //TODO: this hashmap should always be read from file.... but when? That is when it is initialized
    
    public Server(int port){
        connection = new Connection(port);
        connection.start();
    }

    private class Connection extends Thread {
        private int port;
        private ClientHandler clientHandler;

        public Connection(int port){
            this.port = port;
        }

        public void run(){
            //Socket socket = null;
            System.out.println("Server started");
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());
                        clientHandler = new ClientHandler(socket); //create clientHandler for individual client
                    } catch (IOException e) {
                        System.err.println(e);
                        /*
                        if (socket != null)
                            socket.close(); */
                    }
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }

    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private FileController fileController;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            this.fileController = new FileController();
            start();
        }

        public synchronized void run(){
            try{
                while(true){
                    try{
                        Object receivedObj = ois.readObject();
                        if (receivedObj instanceof Message) {
                            Message message = (Message) receivedObj;
                            message.setReceivedTime(new Date()); //set received date
                            System.out.println("read from client: " + message.getText()); //test
                            checkIfOnline(message); //send message to receiver client(s)
                        } else if (receivedObj instanceof User) { //when someone logs in
                            User onlineUser = (User) receivedObj;
                            onlineClients.put(onlineUser, this); //add to online clients hashmap
                            System.out.println(onlineUser.getUsername() + " has logged in");

                            //check if user already exists
                            if (!fileController.checkIfUserAlreadyExists(onlineUser.getUsername(), "all/files/users.txt")) {
                                //if no, save to users.txt file
                                fileController.saveUserToFile(onlineUser.getUsername(), "all/files/users.txt");
                            }else { //if yes
                                //read contacts file
                                //store contacts to user array
                                //TODO: load messages for user from file?
                            }

                            //TODO: send message to other clients that user is online (do this through callback?)

                        }
                    } catch (ClassNotFoundException e){
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e){
                try{
                    socket.close();
                } catch (IOException io){
                    System.err.println(io);
                }
            }

        }

        /**
         * checks if receivers are online.
         * If yes, call to forward message. If no, call to store message in file
         * @param message message to send
         */
        public void checkIfOnline(Message message) {
            //check if any receiver is online or not
            for (String receiver : message.getReceiverList()) {
                for (User receiverUser : onlineClients.keySet()) { //check if registered as online
                    if (receiverUser.getUsername().equals(receiver)) { //if yes
                        System.out.println("message to send to: " + receiver);
                        onlineClients.get(receiverUser).forwardMessage(message); //send to receiver clientHandler
                        break; //end search
                    } else { //if offline
                        storeUnsentMessages(message, receiver);
                    }
                }
            }

        }

        /**
         * forwards message to receiver clients
         */
        public void forwardMessage(Message message) {
            System.out.println("forwarding message to receiver(s)");
            try {
                oos.writeObject(message);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * stores unsent messages in unsentMessagesMap
         */
        public void storeUnsentMessages(Message message, String receiver) {
            if (unsentMessagesMap.containsKey(receiver)) { //if receiver already has unsent messages
                ArrayList<Message> unsentMessages = unsentMessagesMap.get(receiver); //retrieve arraylist
                unsentMessages.add(message); //add new message to list
                unsentMessagesMap.put(receiver, unsentMessages); //update hashmap
            } else {
                ArrayList<Message> unsentMessages = new ArrayList<>(); //create new arraylist
                unsentMessages.add(message); //add new message to list
                unsentMessagesMap.put(receiver, unsentMessages); //add new key-value index
                //TODO: also store to file for later retrieval by offline user? is that to be done here?
            }
        }


    }



    /**
     * starts instance of server on port 724
     * @param args
     */
    public static void main(String[] args) throws ParseException {
        new Server(724);
    }


}
