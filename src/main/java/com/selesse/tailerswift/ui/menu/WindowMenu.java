package com.selesse.tailerswift.ui.menu;

import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.settings.Settings;
import com.selesse.tailerswift.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class WindowMenu {
    private MainFrame mainFrame;
    private JMenu jMenu;

    public WindowMenu(MainFrame mainFrame) {
        this.jMenu = new JMenu("Window");
        this.jMenu.add(createAlwaysOnTopJMenuItem());
        this.mainFrame = mainFrame;
    }

    private JCheckBoxMenuItem createAlwaysOnTopJMenuItem() {
        Settings settings = Program.getInstance().getSettings();
        JCheckBoxMenuItem alwaysOnTopJMenuItem = new JCheckBoxMenuItem("Always on top", settings.isAlwaysOnTop());
        alwaysOnTopJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.META_MASK |
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        alwaysOnTopJMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.toggleAlwaysOnTop();
            }
        });

        return alwaysOnTopJMenuItem;
    }

    public JMenu getJMenu() {
        return jMenu;
    }
}
