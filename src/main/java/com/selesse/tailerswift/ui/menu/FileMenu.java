package com.selesse.tailerswift.ui.menu;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class FileMenu {
    private JMenu jMenu;
    private JFileChooser jFileChooser;
    private JMenuItem addWatchedFileMenuItem;
    private JMenuItem exitMenuItem;

    public FileMenu() {
        this.jMenu = new JMenu("File");
        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {
            jFileChooser = new JFileChooser();

            addWatchedFileMenuItem = createAddWatchedFileJMenuItem();
            jMenu.add(addWatchedFileMenuItem);

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
                int returnVal = jFileChooser.showDialog(null, "Select File");

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser.getSelectedFile();

                } else {
                    // canceled
                }
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
