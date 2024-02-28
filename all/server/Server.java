package all.server;

import all.jointEntity.Message;
import all.jointEntity.User;
import all.server.controllerAndBoundary.FileController;

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
    private HashMap<String, ArrayList<Message>> unsentMessagesMap = new HashMap<>(); //Key: receiver, Value: unsent messages arraylist
    //TODO: this hashmap should always be read from file.... but when? That is when it is initialized
    
    public Server(int port){
        connection = new Connection(port);
        connection.start();
    }



    //TODO: figure out how to show messages in client when user opens chat window for that user.
    //TODO: here they get sent, but where do they go before the user opens a chat window?
    /**
     * retrieves unsent messages from unsentMessagesMap and sends them to receiver
     * @param unsentMessagesMap hashmap containing unsent messages for all offline users
     */
    public void sendUnsentMessages(HashMap <String, ArrayList <Message>>unsentMessagesMap, User receiver){
        if (unsentMessagesMap.containsKey(receiver)) {
            ArrayList<Message> unsentMessages = unsentMessagesMap.get(receiver); //create arraylist of unsent messages from unsentmessagemap with correct receiver
            for (Message unsent : unsentMessages) {
                //TODO: get clienthandler from receiver and call forwardMessage()
            }
            unsentMessagesMap.remove(receiver); //TODO: remove from hashmap?
            //TODO: and then write updated hashmap to file, through fileController?
        }
    }

    /**
     * checks if receivers are online.
     * If yes, call to forward message. If no, call to store message in file
     * @param message message to send
     */
    public void checkIfOnline(Message message){
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
     * stores unsent messages in unsentMessagesMap
     */
    public void storeUnsentMessages (Message message, String receiver){
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

    /**
     * Method that checks the status of the object that was received.
     * @param receivedObj
     * @param clientHandler
     */

    public void checkObjectStatus(Object receivedObj, ClientHandler clientHandler) {

        if(receivedObj instanceof Message){
            Message message = (Message) receivedObj;
            message.setReceivedTime(new Date());
            checkIfOnline(message);
        } else if(receivedObj instanceof User){
            User onlineUser = (User) receivedObj;
            onlineClients.put(onlineUser, clientHandler);
            ArrayList<String> userList = updateOnlineStatus(onlineUser.getUsername());
            for(User user : onlineClients.keySet()){
                onlineClients.get(user).updateOnlineList(userList);
            }
            //TODO: work with filecontroller to receive unsent messages when user was offline
            /*
            HashMap<String, Arraylist<Message>> unsentMessagesMap =
                fileController.readUnsentFile("all/files/unsentMessages.dat");
             */
            //sendUnsentMessages(unsentMessagesMap, onlineUser);
        }

    }

    public ArrayList<String> updateOnlineStatus(String username){

        ArrayList<String> onlineUsers = new ArrayList<>();

        for(User user : onlineClients.keySet()){
            if(!user.getUsername().equals(username)){
                onlineUsers.add(user.getUsername());
            }
        }

        return onlineUsers;

    }

    public void userHasLoggedOut(String username){

        for(User user : onlineClients.keySet()){
            if(user.getUsername().equals(username)){
                onlineClients.remove(user);
            }
        }

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

        public synchronized void run() {
            try {
                while (true) {
                    try {
                        Object receivedObj = ois.readObject();
                        checkObjectStatus(receivedObj, this);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                }
            } catch (IOException e) {
                System.out.println(e);
            }

        }

        public void updateOnlineList(ArrayList<String> userlist){

            try {
                oos.writeObject(userlist);
                oos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        /**
         * forwards message to receiver clients
         */
        public void forwardMessage (Message message){
            System.out.println("forwarding message to receiver(s)");
            try {
                oos.writeObject(message);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
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


