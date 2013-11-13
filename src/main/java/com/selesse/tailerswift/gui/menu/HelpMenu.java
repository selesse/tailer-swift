package com.selesse.tailerswift.gui.menu;

import com.selesse.tailerswift.gui.view.AboutFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class HelpMenu extends AbstractMenu {
    private AboutFrame aboutFrame;

    public HelpMenu() {
        menu = new JMenu("Help");
        menu.add(createAboutMenuItem());
        aboutFrame = new AboutFrame();
    }

    private JMenuItem createAboutMenuItem() {
        JMenuItem displayOptionsMenuItem = new JMenuItem("About");
        displayOptionsMenuItem.setName("About");
        displayOptionsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        displayOptionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aboutFrame.setVisible(true);
            }
        });
        return displayOptionsMenuItem;
    }

}
