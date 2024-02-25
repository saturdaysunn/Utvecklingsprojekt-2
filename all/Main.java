package all;

import all.jointEntity.User;
import all.klient.controller.MessageClient;
import all.klient.view.LoginPanel;
import all.klient.view.MainFrame;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        //new Client("127.0.0.1", 724).writeText("slay"); //TODO: don't need
        new MessageClient("127.0.0.1", 724);
        //new MessageClient("127.0.0.1", 724);
    }
}