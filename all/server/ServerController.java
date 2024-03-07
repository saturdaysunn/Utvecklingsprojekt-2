package all.server;

import all.jointEntity.*;
import all.server.controller.FileController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class ServerController {
    private Connection connection;
    private HashMap<User, ClientHandler> onlineClients; //stores clients and their connections
    private HashMap<String, ArrayList<Message>> unsentMessagesMap;
    private FileController fileController;
    
    public ServerController(int port){
        this.fileController = new FileController();
        unsentMessagesMap = fileController.retrieveUnsentMessages();
        onlineClients = new HashMap<>();
        connection = new Connection(port);
        connection.start();
    }

    /**
     * retrieves unsent messages from unsentMessagesMap and sends them to now online user.
     * @param receiver user to send unsent messages to (the user that just went online).
     */
    public synchronized void sendUnsentMessages(String receiver){

        for(User user : onlineClients.keySet()){
            //System.out.println(user.getUsername());
            System.out.println(onlineClients.get(user));
        }

        try{
            if (unsentMessagesMap.containsKey(receiver)) {
                ArrayList<Message> unsentMessages = unsentMessagesMap.get(receiver); //create arraylist of unsent messages with correct receiver
                UnsentMessages unsent = new UnsentMessages(unsentMessages);

                for(User user : onlineClients.keySet()){
                    if(user.getUsername().equals(receiver)){
                        onlineClients.get(user).sendUnsent(unsent);
                    }
                }
                unsentMessagesMap.remove(receiver); //remove from hashmap as they now should be sent
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    /**
     * checks if intended receivers of message are online.
     * If yes, call to forward message. If no, call to store message in file
     * @param message message to send
     */
    public synchronized void checkIfOnline(Message message){
        for (String receiver : message.getReceiverList()) {
            boolean isOnline = false;
            for (User onlineUser : onlineClients.keySet()) {
                if (onlineUser.getUsername().equals(receiver)) {
                    onlineClients.get(onlineUser).forwardMessage(message);
                    isOnline = true;
                    break;
                }
            }

            if (!isOnline) {
                System.out.println(receiver + " is offline, storing message now");
                storeUnsentMessage(message, receiver);
            }
        }
    }

    /**
     * stores unsent messages in unsentMessagesMap
     */
    public synchronized void storeUnsentMessage(Message message, String receiver){
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

    public synchronized void checkObjectStatus(Object receivedObj, ClientHandler clientHandler) throws IOException {

        if(receivedObj instanceof Message){
            Message message = (Message) receivedObj;
            message.setReceivedTime(new Date()); //set time received by server
            checkIfOnline(message);

        } else if(receivedObj instanceof User){ //logged in
            User onlineUser = (User) receivedObj;
            String username = onlineUser.getUsername();
            System.out.println("User logged in: " + username);
            onlineClients.put(onlineUser, clientHandler);

            //retrieve online users
            for(User user : onlineClients.keySet()){ //for each currently online user
                String currUsername = user.getUsername();
                ArrayList<String> userList = updateOnlineStatus(currUsername); //retrieve list of other online users
                onlineClients.get(user).updateOnlineList(userList); //send updated onlineList to their clientHandler
            }

            //check if new or old user.
            boolean exists = fileController.checkIfUserAlreadyExists(username, "all/files/users.txt");
            if (!exists) {
                fileController.saveUserToFile(username, "all/files/users.txt");
            } else {
                //retrieve contacts
                ArrayList<String> contacts = fileController.getContactsOfUser("all/files/contacts.txt", username); //retrieve contacts for user
                ContactsMessage contactsMessage = new ContactsMessage(contacts);
                clientHandler.sendContacts(contactsMessage); //send contacts to user

                System.out.println(username + "i else satsen innan sendUnsentMessages");
                sendUnsentMessages(username); //send unsent messages to now online user
            }
        } else if (receivedObj instanceof ContactsMessage) { //user logs out
            ContactsMessage updatedContacts = (ContactsMessage) receivedObj;

            ArrayList<String> contacts = updatedContacts.getContactsList();
            fileController.rewriteContactsTextFileWithNewContacts(updatedContacts.getOwner(), contacts);

            //register that user has logged out.
            String loggedOutUser = updatedContacts.getOwner();
            logOutUser(loggedOutUser); //update online clients
        }

    }

    /**
     * returns list of online users excluding user it is to be sent to.
     * @param username username of user list is to be sent to.
     * @return list of other online users
     */
    public synchronized ArrayList<String> updateOnlineStatus(String username){
        ArrayList<String> onlineUsers = new ArrayList<>();

        for(User user : onlineClients.keySet()){ //loop through online clients hashmap
            if(!user.getUsername().equals(username)){ //for each case where
                onlineUsers.add(user.getUsername());
            }
        }
        return onlineUsers;
    }


    /**
     * removes user from onlineClients hashmap.
     * @param username username of user that logged out.
     */
    public synchronized void logOutUser(String username){
        Iterator<User> iterator = onlineClients.keySet().iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getUsername().equals(username)) {
                iterator.remove();
            }
        }
        for(User user : onlineClients.keySet()){ //for each currently online user
            ArrayList<String> userList = updateOnlineStatus(user.getUsername()); //retrieve list of other online users
            onlineClients.get(user).updateOnlineList(userList); //send updated onlineList to their clientHandler
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
            Socket socket = null;
            System.out.println("Server started");
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    try {
                        socket = serverSocket.accept();
                        System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());
                        clientHandler = new ClientHandler(socket); //create clientHandler for individual client
                    } catch (IOException e) {
                        System.err.println(e);

                        if (socket != null)
                            socket.close();
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

        /**
         * sends UnsentMessages object through stream
         * @param unsent object containing arraylist of unsent messages.
         */
        public void sendUnsent(UnsentMessages unsent) {
            try {
                oos.writeObject(unsent);
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


