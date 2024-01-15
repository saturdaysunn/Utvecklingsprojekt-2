package klient.view;

import jointEntity.Message;
import klient.CallBackInterface;
import klient.controller.MessageClient;

/**
 *
 * This class is responsible for being the listener class that implements a callback
 * to update the GUI with the messages received from MessageClient.
 *
 */

public class ChatViewer extends CenterPanel implements CallBackInterface {

    private MessageClient messageClient;

    public ChatViewer(int width, int height, MainFrame mainFrame){
        super(width, height, mainFrame);
        messageClient.addListener(this);

    }

    public void newMessage(Message message){



    }



}
