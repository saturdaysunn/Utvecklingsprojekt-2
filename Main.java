import klient.view.MainFrame;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        //new Client("127.0.0.1", 724).writeText("slay");
        new MainFrame(1000, 600);

    }
}