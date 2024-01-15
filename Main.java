import jointEntity.User;
import klient.view.MainFrame;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        //new Client("127.0.0.1", 724).writeText("slay");
        new MainFrame(1000, 600);


        User User1= new User("User1",new ImageIcon("/"),false);

        User User2= new User("User1",new ImageIcon("/"),false);

        User User3= new User("User1",new ImageIcon("/"),false);

        User User4= new User("User1",new ImageIcon("/"),false);
    }
}