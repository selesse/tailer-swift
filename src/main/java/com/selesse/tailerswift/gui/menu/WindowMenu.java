package com.selesse.tailerswift.gui.menu;

import com.selesse.tailerswift.gui.view.MainFrameView;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class WindowMenu extends AbstractMenu {
    private MainFrameView mainFrameView;

    public WindowMenu(MainFrameView mainFrameView) {
        this.menu = new JMenu("Window");
        this.menu.add(createAlwaysOnTopMenuItem());
        this.mainFrameView = mainFrameView;
    }

    private JCheckBoxMenuItem createAlwaysOnTopMenuItem() {
        Settings settings = Program.getInstance().getSettings();
        JCheckBoxMenuItem alwaysOnTopMenuItem = new JCheckBoxMenuItem("Always on top", settings.isAlwaysOnTop());
        alwaysOnTopMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        alwaysOnTopMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrameView.toggleAlwaysOnTop();
            }
        });

        return alwaysOnTopMenuItem;
    }
}
