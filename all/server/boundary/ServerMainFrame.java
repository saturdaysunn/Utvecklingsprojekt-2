    package all.server.boundary;

    import javax.swing.*;
    import java.awt.event.InputMethodEvent;

    public class ServerMainFrame extends JFrame {

        private ServerMainPanel mainPanel;

        private ServerLeftPanel leftPanel;

        private ServerRightPanel rightPanel;


        public ServerMainFrame(int width, int height) {
            super("Server");
            this.setResizable(false);
            this.setSize(width, height);
            this.mainPanel = new ServerMainPanel(width, height, this);
            this.setContentPane(mainPanel);
            this.setVisible(true);
            this.setBounds(100, 100, width, height);
            this.setDefaultCloseOperation(3);
        }


        public ServerMainPanel getMainPanel() {
            return mainPanel;
        }

        public ServerLeftPanel getLeftPanel() {
            return leftPanel;
        }
    }
