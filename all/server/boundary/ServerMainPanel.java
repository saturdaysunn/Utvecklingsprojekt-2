package all.server.boundary;

import javax.swing.*;
import java.awt.*;

public class ServerMainPanel extends JPanel {

    private ServerRightPanel rPanel;

    private ServerLeftPanel lPanel;

    public ServerMainPanel(int width, int height, ServerMainFrame mainFrame){
        super(new BorderLayout());
        this.setSize(width, height);
        lPanel= new ServerLeftPanel(width -600, height, mainFrame);
        rPanel = new ServerRightPanel(width - 600, height, mainFrame);
        rPanel.setMinimumSize(new Dimension(200, height));
        this.add(rPanel, BorderLayout.EAST);
        this.add(lPanel, BorderLayout.CENTER);
    }





    public ServerRightPanel getrPanel(){
        return rPanel;
    }

    public void setrPanel(ServerRightPanel rPanel){
        this.rPanel = rPanel;
    }

    public ServerLeftPanel getlPanel(){
        return lPanel;
    }






}

