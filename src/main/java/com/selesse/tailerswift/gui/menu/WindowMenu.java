package com.selesse.tailerswift.gui.menu;

import com.selesse.tailerswift.gui.MainFrame;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class WindowMenu extends AbstractMenu {
    private MainFrame mainFrame;

    public WindowMenu(MainFrame mainFrame) {
        this.menu = new JMenu("Window");
        this.menu.add(createAlwaysOnTopMenuItem());
        this.mainFrame = mainFrame;
    }

    private JCheckBoxMenuItem createAlwaysOnTopMenuItem() {
        Settings settings = Program.getInstance().getSettings();
        JCheckBoxMenuItem alwaysOnTopMenuItem = new JCheckBoxMenuItem("Always on top", settings.isAlwaysOnTop());
        alwaysOnTopMenuItem.setName("Always on top");
        alwaysOnTopMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        alwaysOnTopMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.toggleAlwaysOnTop();
            }
        });

        return alwaysOnTopMenuItem;
    }
}
