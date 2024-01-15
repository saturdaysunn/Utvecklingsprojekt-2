package all.klient.view;

import all.jointEntity.Message;
import all.klient.CallBackInterface;
import all.klient.controller.MessageClient;

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
