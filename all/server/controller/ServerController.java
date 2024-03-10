package all.server.controller;

import all.jointEntity.*;
import all.server.boundary.ServerMainFrame;

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
    private ServerMainFrame mainFrame;

    public ServerController(int port){
        this.fileController = new FileController();
        unsentMessagesMap = fileController.retrieveUnsentMessages();
        onlineClients = new HashMap<>();
        connection = new Connection(port);
        mainFrame = new ServerMainFrame(800, 600);
        connection.start();
    }

    /**
     * retrieves unsent messages from unsentMessagesMap and sends them to now online user.
     * @param receiver user to send unsent messages to (the user that just went online).
     */
    public synchronized void sendUnsentMessages(User receiver){
        System.out.println("going to send unsent to " + receiver.getUsername());
        try{
            if (unsentMessagesMap.containsKey(receiver.getUsername())) {
                ArrayList<Message> unsentMessages = unsentMessagesMap.get(receiver.getUsername()); //create arraylist of unsent messages with correct receiver
                UnsentMessages unsent = new UnsentMessages(unsentMessages);
                for(Message unsentMessage : unsent.getUnsentList()){
                    unsentMessage.setDeliveredTime(new Date());
                    fileController.saveLogToFile("Unsent message from: [" + unsentMessage.getSender().getUsername() + "], To: " + unsentMessage.getReceiverList() + " has been sent, Content: " + unsentMessage.getText()
                            + ", Received Time: " + unsentMessage.getReceivedTime() +", Delivered Time: " + unsentMessage.getDeliveredTime(), new Date(),"all/files/log.txt");
                }
                onlineClients.get(receiver).sendUnsent(unsent);
                unsentMessagesMap.remove(receiver.getUsername()); //remove from hashmap as they now should be sent
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
    public synchronized void checkIfOnline(Message message, boolean groupChat){
        if (groupChat) { //only have to send once if group is included in receivers
            String senderName = message.getSender().getUsername();
            for (User onlineUser : onlineClients.keySet()) { //send to everyone except sender
                if (!onlineUser.getUsername().equals(senderName)) {
                    message.setDeliveredTime(new Date());
                    onlineClients.get(onlineUser).forwardMessage(message);
                }
            }
        } else { //send to each individual receiver
            for (String receiver : message.getReceiverList()) {
                boolean isOnline = false;
                for (User onlineUser : onlineClients.keySet()) {
                    if (onlineUser.getUsername().equals(receiver)) {
                        System.out.println(receiver + " is online, sending message now");
                        message.setDeliveredTime(new Date());
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
    }

    /**
     * stores unsent messages in unsentMessagesMap
     */
    public synchronized void storeUnsentMessage(Message message, String receiver){
        if (unsentMessagesMap.containsKey(receiver)) { //if receiver already has unsent messages
            ArrayList<Message> unsentMessages = unsentMessagesMap.get(receiver); //retrieve arraylist
            unsentMessages.add(message); //add new message to list
            unsentMessagesMap.put(receiver, unsentMessages); //update hashmap
            fileController.saveLogToFile("Message from: [" + message.getSender().getUsername() + "], To: [" + receiver + "] has been stored as unsent", new Date(), "all/files/log.txt");
        } else {
            ArrayList<Message> unsentMessages = new ArrayList<>(); //create new arraylist
            unsentMessages.add(message); //add new message to list
            unsentMessagesMap.put(receiver, unsentMessages); //add new key-value index
            fileController.saveLogToFile("Message from: [" + message.getSender().getUsername() + "], To: [" + receiver + "] has been stored as unsent", new Date(), "all/files/log.txt");
        }
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
            boolean groupChat = checkIfGroupChat(message.getReceiverList());
            checkIfOnline(message, groupChat);
            fileController.saveLogToFile("Message from: [" + message.getSender().getUsername() + "], To: " + message.getReceiverList() + ", Content: " + message.getText()
                    + ", Received Time: " + message.getReceivedTime() +", Delivered Time: " + message.getDeliveredTime(), new Date(),"all/files/log.txt");
        } else if(receivedObj instanceof User){ //logged in
            User loggedInUser = (User) receivedObj;
            String username = loggedInUser.getUsername();
            System.out.println("User logged in: " + username);
            onlineClients.put(loggedInUser, clientHandler);
            fileController.saveLogToFile("[" + loggedInUser.getUsername() + "] has logged in", new Date(), "all/files/log.txt");

            //retrieve online users
            for(User onlineUser : onlineClients.keySet()){ //for each currently online user
                String currUsername = onlineUser.getUsername();
                ArrayList<String> userList = updateOnlineStatus(currUsername); //retrieve list of other online users
                onlineClients.get(onlineUser).updateOnlineList(userList); //send updated onlineList to their clientHandler
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
                sendUnsentMessages(loggedInUser); //send unsent messages to now online user
            }
        } else if (receivedObj instanceof ContactsMessage) { //user logged out
            ContactsMessage updatedContacts = (ContactsMessage) receivedObj;
            ArrayList<String> contacts = updatedContacts.getContactsList();
            if(contacts != null) {
                fileController.rewriteContactsTextFileWithNewContacts(updatedContacts.getOwner(), contacts);
            }
            fileController.saveLogToFile("Contacts updated for: [" + updatedContacts.getOwner() + "] ", new Date(), "all/files/log.txt");
            //register that user has logged out.
            String loggedOutUser = updatedContacts.getOwner();
            logOutUser(loggedOutUser); //update online clients
        }

        fileController.updateUnsentMessages(unsentMessagesMap);

    }

    /**
     * checks if receiver list contains group chat.
     * @param receivers list of receivers
     * @return true if group chat, else false
     */
    public boolean checkIfGroupChat(ArrayList<String> receivers) {
        for (String receiver : receivers) {
            if (receiver.equals("GroupChat")) {
                return true;
            }
        }
        return false;
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
        fileController.saveLogToFile("[" + username + "] has logged out", new Date(),"all/files/log.txt");
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
                        fileController.saveLogToFile("New client connected: " + socket.getInetAddress().getHostAddress(), new Date() ,"all/files/log.txt");
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


