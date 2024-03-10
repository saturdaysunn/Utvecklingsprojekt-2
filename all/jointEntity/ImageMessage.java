package all.jointEntity;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;


public class ImageMessage extends Message {
    private ImageIcon image;

    public ImageMessage(User sender, ArrayList<String> receiverList,
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
