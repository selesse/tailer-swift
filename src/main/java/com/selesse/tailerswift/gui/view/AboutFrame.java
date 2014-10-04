package com.selesse.tailerswift.gui.view;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class AboutFrame extends JFrame {
    private static Logger LOGGER = LoggerFactory.getLogger(AboutFrame.class);

    public AboutFrame() {
        super("About");
        this.setName("About");
        this.setPreferredSize(new Dimension(600, 700));
        this.setLayout(new GridBagLayout());

        final JButton urlButton = new JButton("http://github.com/selesse/tailer-swift");
        urlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(URI.create(urlButton.getText()));
                        LOGGER.debug("User clicked on the GitHub link");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        JLabel openSourceLabel = new JLabel("This project uses open source software:", SwingConstants.CENTER);

        JTextArea licenseTextArea = new JTextArea();
        licenseTextArea.setText(getOpenSourceLicenseText());
        JScrollPane scrollPane = new JScrollPane(licenseTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        licenseTextArea.setCaretPosition(0);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.insets = new Insets(15, 15, 15, 15);
        constraints.fill = GridBagConstraints.NONE;
        this.add(urlButton, constraints);

        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(openSourceLabel, constraints);

        constraints.gridy = 2;
        constraints.gridheight = 10;
        constraints.weighty = 1.0;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        this.add(scrollPane, constraints);


        this.pack();
        this.setLocationRelativeTo(null);
    }

    private String getOpenSourceLicenseText() {
        String licenseText = "";
        URL guavaUrl = Resources.getResource("licenses/guava.txt");
        URL slf4jUrl = Resources.getResource("licenses/slf4j.txt");

        try {
            String guavaContents = Resources.toString(guavaUrl, Charsets.UTF_8);
            String slf4jContents = Resources.toString(slf4jUrl, Charsets.UTF_8);

            licenseText = guavaContents + "\n\n" + slf4jContents;
        } catch (IOException e) {
            LOGGER.error("Could not open license", e);
        }

        return licenseText;
    }
}
