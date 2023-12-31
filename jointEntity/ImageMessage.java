package jointEntity;

import jointEntity.Message;
import jointEntity.TextMessage;
import jointEntity.User;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;

public class ImageMessage extends TextMessage {

    private ImageIcon image;

    public ImageMessage(User sender, ArrayList<User> receiverList,
                        String text, Date receivedTime, Date deliveredTime, ImageIcon image){

        super(sender, receiverList, text, receivedTime, deliveredTime);
        this.image = image;

    }

    public ImageIcon getImage() {
        return image;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }

}
