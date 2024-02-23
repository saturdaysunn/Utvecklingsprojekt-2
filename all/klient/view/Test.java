package all.klient.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.zip.*;

public class Test extends JFrame {

    private JButton selectFileButton;
    private JLabel imageLabel;

    public Test() {
        setTitle("Image File Saver");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        selectFileButton = new JButton("Select Image");
        imageLabel = new JLabel();

        selectFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    loadImage(selectedFile);
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(selectFileButton);

        JScrollPane scrollPane = new JScrollPane(imageLabel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void loadImage(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            ImageIcon icon = new ImageIcon(image);
            imageLabel.setIcon(icon);
            saveImageToFile(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveImageToFile(BufferedImage image) {
        try {
            File outputfile = new File("output.png");
            ImageIO.write(image, "png", outputfile);
            JOptionPane.showMessageDialog(this, "Image saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Test().setVisible(true);
            }
        });
    }
}
