package all;

import all.jointEntity.User;
import all.klient.controller.MessageClient;
import all.klient.view.LoginPanel;
import all.klient.view.MainFrame;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        new LoginPanel();
        new LoginPanel();
        //TODO: how to make it so that they are created independently and can be closed independently?
        //TODO: call e.g. this.dispose() when clicking log out?
    }
}