package com.selesse.tailerswift.gui.menu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class HelpMenu extends AbstractMenu {
    public HelpMenu() {
        menu = new JMenu("Help");
        menu.add(createAboutMenuItem());
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

}
