package com.selesse.tailerswift.gui.menu;

import com.selesse.tailerswift.gui.MainFrame;
import com.selesse.tailerswift.gui.displayoptions.DisplayPreferencesFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class SettingsMenu extends AbstractMenu {
    private DisplayPreferencesFrame displayPreferencesFrame;
    private MainFrame mainFrame;

    public SettingsMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        menu = new JMenu("Settings");
        menu.add(createDisplayOptionsMenuItem());
    }

    private JMenuItem createDisplayOptionsMenuItem() {
        JMenuItem displayOptionsMenuItem = new JMenuItem("Display preferences");
        displayOptionsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        displayOptionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (displayPreferencesFrame == null) {
                            displayPreferencesFrame = new DisplayPreferencesFrame(mainFrame);
                        }
                        displayPreferencesFrame.focus();
                    }
                }
                );
            }
        });
        return displayOptionsMenuItem;
    }
}
