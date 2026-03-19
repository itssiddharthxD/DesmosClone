package com.desmosclone.main;

import javax.swing.*;
import java.awt.*;

public class DesmosClone extends JFrame {

    private JTextField inputField;
    private GraphPanel graphPanel;

    public DesmosClone() {
        setTitle("Desmos Clone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // --- Top Control Panel ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(240, 240, 240));

        JLabel label = new JLabel("f(x) = ");
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        inputField = new JTextField("sin(x)");
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        inputField.addActionListener(e -> graphPanel.setEquation(inputField.getText()));

        topPanel.add(label, BorderLayout.WEST);
        topPanel.add(inputField, BorderLayout.CENTER);

        
        graphPanel = new GraphPanel();
        graphPanel.setEquation(inputField.getText());

        
        add(topPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DesmosClone().setVisible(true);
        });
    }
}
