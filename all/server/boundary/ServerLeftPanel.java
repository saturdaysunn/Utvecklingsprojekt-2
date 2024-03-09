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
        textArea.setEditable(false); // Making the text area non-editable
        textArea.setLineWrap(true); // Enabling line wrap to prevent horizontal scrolling
        textArea.setWrapStyleWord(true); // Ensuring words won't be broken at line breaks
        JScrollPane scrollPane = new JScrollPane(textArea);

        textArea.setPreferredSize(new Dimension(400, height));
        // Setting preferred size to make the text area wider
        scrollPane.setPreferredSize(new Dimension(300, height));

        this.add(scrollPane, BorderLayout.CENTER);
    }

    // Metod för att lägga till meddelanden till textområdet
    public void logMessage(String message) {
        JTextArea textArea = (JTextArea) ((JScrollPane) this.getComponent(0)).getViewport().getView();
        textArea.append(message + "\n");
    }

    public void clearLog() {
        JTextArea textArea = (JTextArea) ((JScrollPane) this.getComponent(0)).getViewport().getView();
        textArea.setText("");
    }
}