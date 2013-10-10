package com.selesse.tailerswift.ui.menu;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class FileMenu {
    private JMenu jMenu;
    private FileDialog fileDialog;
    private MainFrame mainFrame;

    public FileMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.jMenu = new JMenu("File");

        fileDialog = new FileDialog(mainFrame.getJFrame());

        JMenuItem addWatchedFileMenuItem = createAddWatchedFileJMenuItem();
        jMenu.add(addWatchedFileMenuItem);

        // OS X handles CMD + Q automatically
        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {
            JMenuItem exitMenuItem = createExitJMenuItem();
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
                String chosenFileName = fileDialog.getFile();
                if (chosenFileName != null) {
                    File chosenFile = new File(fileDialog.getDirectory() + File.separator + chosenFileName);
                    mainFrame.startWatching(chosenFile);
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
