package all.server.boundary;

import javax.swing.*;
import java.awt.*;
import java.io.StringWriter;

public class ServerLeftPanel extends JPanel {
    private int width;
    private int height;

    private ServerMainFrame mainFrame;

    public ServerLeftPanel(int width, int height, ServerMainFrame mainFrame) {
        super(new BorderLayout());
        this.setSize(width, height);
        this.width = width;
        this.height = height;
        this.mainFrame = mainFrame;

        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea); // Lägger till en rullningsfält till textarean

        this.add(scrollPane, BorderLayout.CENTER);
    }

    // Metod för att lägga till meddelanden till textområdet
    public void logMessage(String message) {
        JTextArea textArea = (JTextArea) ((JScrollPane) this.getComponent(0)).getViewport().getView();
        textArea.append(message + "\n");
    }
}