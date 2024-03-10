package all.server.boundary;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        textArea.setPreferredSize(new Dimension(400, height));
        scrollPane.setPreferredSize(new Dimension(300, height));

        this.add(scrollPane, BorderLayout.CENTER);
    }
    
    public void logMessage(String message) {
        JTextArea textArea = (JTextArea) ((JScrollPane) this.getComponent(0)).getViewport().getView();
        textArea.append(message + "\n");
    }

    public void updateLogMessages(List<String> messages) {
        JTextArea textArea = (JTextArea) ((JScrollPane) this.getComponent(0)).getViewport().getView();
        textArea.setText(""); //remove existing text to replace with new log messages
        for (String message : messages) {
            textArea.append(message + "\n");
        }
    }
}