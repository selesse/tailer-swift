package com.selesse.tailerswift.gui.view;

import javax.swing.*;
import java.awt.*;

public class AboutFrame extends JFrame {
    private JLabel aboutLabel = new JLabel("http://github.com/selesse/tailer-swift");
    private JLabel disclaimer = new JLabel("This project uses open source software:");

    public AboutFrame() {
        super("About");
        this.setName("About");
        this.setPreferredSize(new Dimension(500, 500));

        this.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new BorderLayout());
        aboutLabel.setHorizontalAlignment(SwingConstants.CENTER);
        disclaimer.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(aboutLabel, BorderLayout.NORTH);
        panel.add(disclaimer, BorderLayout.SOUTH);
        this.add(panel, BorderLayout.NORTH);
        this.add(new JTextArea("Software licenses here"), BorderLayout.CENTER);

        this.pack();
        this.setLocationRelativeTo(null);
    }
}
