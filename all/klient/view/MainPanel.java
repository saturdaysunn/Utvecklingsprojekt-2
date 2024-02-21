package all.klient.view;

import javax.swing.*;
import java.awt.*;
import all.klient.view.*;

public class MainPanel extends JPanel {
    private LPanel lPanel;
    private RPanel rPanel;
    private CenterPanel cPanel;

    public MainPanel(int width, int height, MainFrame mainFrame){
        super(null);
        this.setSize(width, height);

        lPanel = new LPanel(-800, height, mainFrame);
        this.add(lPanel);

        cPanel = new CenterPanel(width - 400, height, mainFrame);
        this.add(cPanel);

        rPanel = new RPanel(width - 600, height, mainFrame);
        this.add(rPanel);

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

}