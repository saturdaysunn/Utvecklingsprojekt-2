        package all.server.boundary;

        import all.server.controller.FileController;

        import javax.swing.*;
        import java.awt.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.util.List;

        public class ServerRightPanel extends JPanel {

            private final int width;
            private int height;

            private ServerMainFrame mainFrame;

            private FileController fileController = new FileController();


            public ServerRightPanel(int width, int height, ServerMainFrame mainFrame) {
                super(new BorderLayout());
                this.setSize(width, height);
                this.width = width;
                this.height = height;
                this.mainFrame = mainFrame;

                JLabel instructionsLabel = new JLabel("Ange tider för att ta ut det i textarean", SwingConstants.CENTER);
                instructionsLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14)); // Välj en rolig font
                instructionsLabel.setBorder(BorderFactory.createEmptyBorder(300, 0, 0, 0)); // Lägg till mellanrum runt texten
                add(instructionsLabel, BorderLayout.NORTH);

                // Skapa etiketter
                JLabel label1 = new JLabel("Ange första tiden:");
                JLabel label2 = new JLabel("Ange andra tiden:");

                // Skapa textfälten med större bredd
                JTextField textField1 = new JTextField();
                textField1.setColumns(10); // Sätt antalet synliga tecken till 10
                JTextField textField2 = new JTextField();
                textField2.setColumns(10); // Sätt antalet synliga tecken till 10

                // Skapa submit-knappen
                JButton submitButton = new JButton("Submit");
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String text1 = textField1.getText();
                        String text2 = textField2.getText();

                    }
                });

                JPanel inputPanel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.insets = new Insets(5, 5, 5, 5);

                inputPanel.add(label1, gbc);

                gbc.gridx = 1;
                inputPanel.add(textField1, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                inputPanel.add(label2, gbc);

                gbc.gridx = 1;
                inputPanel.add(textField2, gbc);


                JPanel buttonPanel = new JPanel();
                buttonPanel.add(submitButton);

                JPanel rightPanel = new JPanel(new BorderLayout());
                rightPanel.add(inputPanel, BorderLayout.CENTER);
                rightPanel.add(buttonPanel, BorderLayout.SOUTH);

                this.add(rightPanel, BorderLayout.EAST);
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String startTime = textField1.getText();
                        String endTime = textField2.getText();
                        List<String> messages = fileController.getLogMessagesBetweenTimes(startTime, endTime, "all/files/log.txt");
                        mainFrame.getLeftPanel().clearLog(); // Clear existing log
                        for (String message : messages) {
                            mainFrame.getLeftPanel().logMessage(message);
                        }
                    }
                });

            }
        }
