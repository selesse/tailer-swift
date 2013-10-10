package com.selesse.tailerswift.ui.menu;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class FileMenu {
    private JMenu jMenu;
    private FileDialog fileDialog;
    private JMenuItem addWatchedFileMenuItem;
    private JMenuItem exitMenuItem;

    public FileMenu(JFrame parentFrame) {
        this.jMenu = new JMenu("File");

        fileDialog = new FileDialog(parentFrame);

        addWatchedFileMenuItem = createAddWatchedFileJMenuItem();
        jMenu.add(addWatchedFileMenuItem);

        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {

            exitMenuItem = createExitJMenuItem();
            jMenu.add(exitMenuItem);
        }
    }

    private JMenuItem createAddWatchedFileJMenuItem() {
        JMenuItem addWatchedFileMenuItem = new JMenuItem("Watch File...");
        addWatchedFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        addWatchedFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileDialog.setVisible(true);
                fileDialog.getFile();
            }
        });

        return addWatchedFileMenuItem;
    }

    private JMenuItem createExitJMenuItem() {
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
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
