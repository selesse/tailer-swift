package com.selesse.tailerswift.gui.menu;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Custom wrapper for either a {@link java.awt.FileDialog} or a {@link javax.swing.JFileChooser}.
 * FileDialogs don't play nice with FEST, so we only use them with OS X (where FEST doesn't appear to work anyway).
 */
public class CrossPlatformFileChooser {
    private Container fileChooserImpl;
    private Frame parentFrame;

    public CrossPlatformFileChooser(Frame frame) {
        this.parentFrame = frame;
        if (Program.getInstance().getOperatingSystem() == OperatingSystem.MAC) {
            fileChooserImpl = new FileDialog(frame);
        }
        else {
            fileChooserImpl = new JFileChooser();
        }
    }

    public void setName(String name) {
        fileChooserImpl.setName(name);
    }

    public void setVisible(boolean visible) {
        fileChooserImpl.setVisible(visible);
    }

    public void setMultiSelectionEnabled(boolean multiSelectionEnabled) {
        if (fileChooserImpl instanceof JFileChooser) {
            ((JFileChooser) fileChooserImpl).setMultiSelectionEnabled(multiSelectionEnabled);
        }
        else {
            ((FileDialog) fileChooserImpl).setMultipleMode(multiSelectionEnabled);
        }
    }

    public File[] getSelectedFiles() {
        File[] selectedFiles;
        if (fileChooserImpl instanceof JFileChooser) {
            selectedFiles = ((JFileChooser) fileChooserImpl).getSelectedFiles();
        }
        else {
            selectedFiles = ((FileDialog) fileChooserImpl).getFiles();
        }
        return selectedFiles;
    }

    public boolean hasSelectedFile() {
        if (fileChooserImpl instanceof JFileChooser) {
            int returnStatus = ((JFileChooser) fileChooserImpl).showOpenDialog(parentFrame);
            return returnStatus == JFileChooser.APPROVE_OPTION;
        }
        else {
            File[] chosenFileNames = ((FileDialog) fileChooserImpl).getFiles();
            return chosenFileNames.length > 0;
        }
    }
}
