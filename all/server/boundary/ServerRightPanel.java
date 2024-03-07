        package all.server.boundary;

        import javax.swing.*;
        import java.awt.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;

        public class ServerRightPanel extends JPanel {

            private final int width;
            private int height;

            private ServerMainFrame mainFrame;


            public ServerRightPanel(int width, int height, ServerMainFrame mainFrame) {
                super(new BorderLayout());
                this.setSize(width, height);
                this.width = width;
                this.height = height;
                this.mainFrame = mainFrame;

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
                        // Implementera här vad som ska hända när knappen trycks
                        // Till exempel, hämta värdet från textfälten
                        String text1 = textField1.getText();
                        String text2 = textField2.getText();
                        // Göra något med dessa värden
                    }
                });

                // Skapa en panel för textfält och etiketter och använd FlowLayout för att de ska ligga parallellt
                JPanel inputPanel = new JPanel(new FlowLayout());
                inputPanel.add(label1);
                inputPanel.add(textField1);
                inputPanel.add(label2);
                inputPanel.add(textField2);

                // Placera submit-knappen i en annan panel
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(submitButton);

                // Lägg till både inputPanel och buttonPanel i en sammansatt panel
                JPanel rightPanel = new JPanel(new BorderLayout());
                rightPanel.add(inputPanel, BorderLayout.CENTER);
                rightPanel.add(buttonPanel, BorderLayout.SOUTH);

                this.add(rightPanel, BorderLayout.EAST);
            }



        }
