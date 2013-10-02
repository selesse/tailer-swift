package com.selesse.tailerswift.ui.menu;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class FileMenu {
    private JMenu jMenu;

    public FileMenu() {
        this.jMenu = new JMenu("File");
        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {
            jMenu.add(createExitJMenuItem());
        }
    }

    private JMenuItem createExitJMenuItem() {
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.META_MASK |
                (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())));
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });

        return exitMenuItem;
    }

    public JMenu getJMenu() {
        return jMenu;
    }
}
