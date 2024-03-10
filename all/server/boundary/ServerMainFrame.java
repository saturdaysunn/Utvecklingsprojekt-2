package all.server.boundary;

import javax.swing.*;

public class ServerMainFrame extends JFrame {
    private ServerMainPanel mainPanel;
    private ServerLeftPanel leftPanel;

    public ServerMainFrame(int width, int height) {
        super("Server");
        this.setResizable(false);
        this.setSize(width, height);
        this.mainPanel = new ServerMainPanel(width, height, this);
        this.setContentPane(mainPanel);
        this.setVisible(true);
        this.setBounds(100, 100, width, height);
        this.setDefaultCloseOperation(3);
        this.leftPanel = new ServerLeftPanel(width - 600, height, this);
    }


    public ServerMainPanel getMainPanel() {
        return mainPanel;
    }

    public ServerLeftPanel getleftPanel() {
        return leftPanel;
    }

}
