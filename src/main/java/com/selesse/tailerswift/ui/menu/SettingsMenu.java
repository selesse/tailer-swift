package com.selesse.tailerswift.ui.menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class SettingsMenu {
    private JMenu jMenu;

    public SettingsMenu() {
        this.jMenu = new JMenu("Settings");
        this.jMenu.add(createDisplayOptionsJMenuItem());
    }

    private JMenuItem createDisplayOptionsJMenuItem() {
        JMenuItem displayOptionsMenuItem = new JMenuItem("Display preferences");
        displayOptionsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        displayOptionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO open display preferences
            }
        });
        return displayOptionsMenuItem;
    }

    public JMenu getJMenu() {
        return jMenu;
    }
}
