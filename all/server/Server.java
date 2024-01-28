package all.server;

import all.jointEntity.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private Connection connection;

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

            Socket socket = null;
            System.out.println("Server startad");

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    try {
                        socket = serverSocket.accept();
                        clientHandler = new ClientHandler(socket);
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

        public void run(){

            Message message;

            try{
                while(true){
                    try{
                        message = (Message) ois.readObject();
                        oos.writeObject(message);
                        oos.flush();
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

    }

    private class Connected extends Thread {

        public void run(){

            /*
                kolla efter uppkopplade klienter
             */

        }

    }

    // kan hända att man behöver en tredje inre klass

    public static void main(String[] args){

        new Server(724);

    }

}
