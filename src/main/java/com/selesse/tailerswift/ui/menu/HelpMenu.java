package com.selesse.tailerswift.ui.menu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class HelpMenu {
    private JMenu jMenu;

    public HelpMenu() {
        this.jMenu = new JMenu("Help");
        this.jMenu.add(createAboutMenuItem());
    }

    private JMenuItem createAboutMenuItem() {
        JMenuItem displayOptionsMenuItem = new JMenuItem("About");
        displayOptionsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        displayOptionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO show about
            }
        });
        return displayOptionsMenuItem;
    }

    public JMenu getJMenu() {
        return jMenu;
    }
}
