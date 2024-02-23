package all.klient.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class Tester extends JFrame {
    private JList<String> list;
    private DefaultListModel<String> listModel;

    public Tester() {
        super("Checkbox JList Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);

        // Initialize the list model
        listModel = new DefaultListModel<>();
        listModel.addElement("Item 1");
        listModel.addElement("Item 2");
        listModel.addElement("Item 3");
        listModel.addElement("Item 4");

        // Initialize the JList with the list model
        list = new JList<>(listModel);

        // Set the cell renderer to display checkboxes
        list.setCellRenderer(new CheckboxListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(list);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tester example = new Tester();
            example.setVisible(true);
        });
    }

    // Custom cell renderer for displaying checkboxes next to list elements
    private class CheckboxListCellRenderer extends JCheckBox implements ListCellRenderer<String> {
        private static final long serialVersionUID = 1L;

        public CheckboxListCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list,
                                                      String value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            setText(value);
            setSelected(isSelected);
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return this;
        }
    }
}
