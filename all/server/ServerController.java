package all.server;

import all.jointEntity.ContactsMessage;
import all.jointEntity.Message;
import all.jointEntity.User;
import all.server.controller.FileController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ServerController {
    private Connection connection;
    private HashMap<User, ClientHandler> onlineClients = new HashMap<>(); //stores clients and their connections
    private HashMap<String, ArrayList<Message>> unsentMessagesMap = new HashMap<>(); //receiver, unsent messages
    private FileController fileController;
    
    public ServerController(int port){
        this.fileController = new FileController();
        connection = new Connection(port);
        connection.start();
    }


    //TODO: figure out how to show messages in client when user opens chat window for that user.
    //TODO: here they get sent, but where do they go before the user opens a chat window?
    /**
     * retrieves unsent messages from unsentMessagesMap and sends them to now online user.
     * @param unsentMessagesMap hashmap containing unsent messages for all offline users.
     * @param receiver user to send unsent messages to (the user that just went online).
     */
    public void sendUnsentMessages(HashMap <String, ArrayList <Message>>unsentMessagesMap, User receiver){
        if (unsentMessagesMap.containsKey(receiver)) {
            ArrayList<Message> unsentMessages = unsentMessagesMap.get(receiver); //create arraylist of unsent messages from unsentmessagemap with correct receiver
            for (Message unsent : unsentMessages) {
                onlineClients.get(receiver).forwardMessage(unsent); //send unsent message to receiver clientHandler
            }
            unsentMessagesMap.remove(receiver); //remove from hashmap as they now should be sent
            //TODO: and then write updated unsentMessages hashmap to file, through fileController.
            //fileController.updateUnsent();
        }
    }

    /**
     * checks if intended receivers of message are online.
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
        }
        fileController.storeUnsentMessages(unsentMessagesMap); //store unsent messages in file through fileController
    }

    /**
     * Method that checks the status of the object that was received.
     * @param receivedObj object received through stream.
     * @param clientHandler ClientHandler instance associated with client sending object.
     */

    public void checkObjectStatus(Object receivedObj, ClientHandler clientHandler) {
        if(receivedObj instanceof Message){
            Message message = (Message) receivedObj;
            message.setReceivedTime(new Date());
            checkIfOnline(message);
        } else if(receivedObj instanceof User){
            User onlineUser = (User) receivedObj;
            onlineClients.put(onlineUser, clientHandler);

            //retrieve online users
            for(User user : onlineClients.keySet()){ //for each currently online user
                ArrayList<String> userList = updateOnlineStatus(user.getUsername()); //retrieve list of other online users
                onlineClients.get(user).updateOnlineList(userList); //send updated onlineList to their clientHandler
            }

            boolean exists = fileController.checkIfUserAlreadyExists(onlineUser.getUsername(), "all/files/contacts.txt");
            if (!exists) {
                fileController.saveUserToFile(onlineUser.getUsername(), "all/files/contacts.txt");
            } else {
                //retrieve contacts
                ArrayList<String> contacts = retrieveContacts(onlineUser); //retrieve contacts for user
                ContactsMessage contactsMessage = new ContactsMessage(contacts); //TODO: does this seem nice?
                clientHandler.sendContacts(contactsMessage); //send contacts to user

                //retrieve possible unsent messages
                //TODO: work with FileController to receive unsent messages when user was offline
            /*
            HashMap<String, Arraylist<Message>> unsentMessagesMap =
                fileController.readUnsentFile("all/files/unsentMessages.dat");
             */
                sendUnsentMessages(unsentMessagesMap, onlineUser); //send unsent messages to now online user
            }
        }

    }

    /**
     * returns list of online users excluding user it is to be sent to.
     * @param username username of user list is to be sent to.
     * @return list of other online users
     */
    public ArrayList<String> updateOnlineStatus(String username){
        ArrayList<String> onlineUsers = new ArrayList<>();

        for(User user : onlineClients.keySet()){ //loop through online clients hashmap
            if(!user.getUsername().equals(username)){ //for each case where
                onlineUsers.add(user.getUsername());
            }
        }
        return onlineUsers;
    }

    /**
     * retrieves user's contacts from file and returns them
     */
    public ArrayList<String> retrieveContacts(User user) {
        ArrayList<String> contacts = fileController.getContactsOfUser("all/files/contacts.txt", user.getUsername());
        if (!contacts.isEmpty()) {
            return contacts;
        } else {
            return null;
        }
    }


    //TODO: this should be called somewhere when user logs out.
    // possibly if user sends their name as string to server as a last thing and then server calls this method.
    //TODO: and then server updates all other clients with update online status etc again, as above.
    /**
     * removes user from onlineClients hashmap when user logs out.
     * @param username username of user that has logged out.
     */
    public void userHasLoggedOut(String username){
        for(User user : onlineClients.keySet()){
            if(user.getUsername().equals(username)){
                onlineClients.remove(user);
            }
        }
    }

    /**
     * inner class responsible for connection between server and client.
     */
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

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            start();
        }

        public synchronized void run() {
            try {
                while (true) {
                    try {
                        Object receivedObj = ois.readObject(); //server boundary receives message from client.
                        checkObjectStatus(receivedObj, this); //send to clientHandler (server controller)
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }

        /**
         * sends list of online clients to all clients.
         * Called every time a new user logs in.
         * @param userList list of all online users (except the user themselves).
         */
        public void updateOnlineList(ArrayList<String> userList){
            try {
                oos.writeObject(userList);
                oos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * forwards message to receiver clients.
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

        /**
         * sends a user's contacts to the user.
         * @param contactsMessage message containing list of contacts
         */
        public void sendContacts(ContactsMessage contactsMessage) {
            try {
                oos.writeObject(contactsMessage);
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
    public static void main(String[] args) {
        new ServerController(724);
    }


}


