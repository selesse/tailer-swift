package com.selesse.tailerswift.gui.menu;

import com.selesse.tailerswift.gui.MainFrame;
import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class FileMenu extends AbstractMenu {
    private JFileChooser fileChooser;
    private MainFrame mainFrame;

    public FileMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        menu = new JMenu("File");
        menu.setName("Menu");

        fileChooser = new JFileChooser();
        fileChooser.setName("File chooser");
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

        menu.add(createAddWatchedFileMenuItem());
        menu.add(createCloseCurrentFileMenuItem());

        // OS X already handles CMD + Q
        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {
            JMenuItem exitMenuItem = createExitMenuItem();
            menu.add(exitMenuItem);
        }
    }

    private JMenuItem createAddWatchedFileMenuItem() {
        JMenuItem addWatchedFileMenuItem = new JMenuItem("Open/watch file...");
        addWatchedFileMenuItem.setName("Open/watch file...");
        addWatchedFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        addWatchedFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnStatus = fileChooser.showOpenDialog(mainFrame.getFrame());

                if (returnStatus == JFileChooser.APPROVE_OPTION) {
                    for (File file : fileChooser.getSelectedFiles()) {
                        mainFrame.startWatching(file);
                    }
                }
            }
        });

        return addWatchedFileMenuItem;
    }

    private JMenuItem createCloseCurrentFileMenuItem() {
        JMenuItem closeCurrentFileMenuItem = new JMenuItem("Close current file");
        closeCurrentFileMenuItem.setName("Close current file");
        closeCurrentFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        closeCurrentFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.closeCurrentTab();
            }
        });
        return closeCurrentFileMenuItem;
    }

    private JMenuItem createExitMenuItem() {
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("Exit");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return exitMenuItem;
    }
}
