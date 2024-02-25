package all.server;

import all.jointEntity.Message;
import all.jointEntity.User;
import all.klient.controller.UserController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private Connection connection;
    private HashMap<String, ClientHandler> clients = new HashMap<>(); //stores clients and their connections
    private HashMap<User, ArrayList<Message>> userArrayListHashMap; //TODO: ??


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
        private UserController userController;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            this.userController = new UserController(); //TODO: correct?
            start();
        }

        public void run(){
            try{
                while(true){
                    try{
                        Object receivedObj = ois.readObject();
                        if (receivedObj instanceof Message) {
                            Message message = (Message) receivedObj;
                            System.out.println("read from client: " + message.getText()); //test
                            forwardMessage(message);
                        } else if (receivedObj instanceof User) { //when someone logs in
                            User onlineUser = (User) receivedObj;

                            //TODO: issue where message is received before name has been set in gui?
                            System.out.println(onlineUser.getUsername() + " has logged in");
                            //TODO: register in online users array

                            //check if user already exists
                            if (!userController.checkIfUserAlreadyExists(onlineUser.getUsername(), "all/files/users.txt")) {
                                //if no, save to users.txt file
                                userController.saveUserToFile(onlineUser.getUsername(), "all/files/users.txt");
                            }else {
                                //TODO: if yes, load contents for user from files (messages, contacts)
                                //TODO: how?
                            }

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
         * forwards message from server to receiving client(s)
         * @param message message to send
         */
        public void forwardMessage(Message message) {
            System.out.println("here to forward message");
            //TODO: inform client that they have a message

            //TODO: check if message is private or group chat

            //TODO: groupchat
            // loop through all online users and send message

            //TODO: individual
            // check if user online, if yes, send message to their clientHandler
            // if no, store message in SOMETHING (unusure, hashmap & file etc?)

        }
    }


    /**
     * starts instance of server on port 724
     * @param args
     */
    public static void main(String[] args){
        new Server(724);
    }

}
