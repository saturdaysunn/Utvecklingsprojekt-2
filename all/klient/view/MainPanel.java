package all.klient.view;

import javax.swing.*;
import java.awt.*;
import all.klient.view.*;

public class MainPanel extends JPanel {

    private LPanel lPanel;
    private RPanel rPanel;
    private CenterPanel cPanel;
    private LoginPanel logPanel;

    public MainPanel(int width, int height, MainFrame mainFrame){

        super(null);
        this.setSize(width, height);
        //logPanel = new LoginPanel(width, height, mainFrame);
        //this.add(logPanel);

        lPanel = new LPanel(-800, height, mainFrame);
        this.add(lPanel);

        cPanel = new CenterPanel(width - 400, height, mainFrame);
        this.add(cPanel);

        rPanel = new RPanel(width - 600, height, mainFrame);
        this.add(rPanel);

        setBackground(new Color(255, 255, 255));

    }

    protected LPanel getlPanel(){
        return lPanel;
    }

    protected RPanel getrPanel(){
        return rPanel;
    }

    protected CenterPanel getcPanel() {
        return cPanel;
    }

    protected LoginPanel getLogPanel(){return logPanel;}

}