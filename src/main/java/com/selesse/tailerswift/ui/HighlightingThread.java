package com.selesse.tailerswift.ui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.List;

public class HighlightingThread implements Runnable {
    private String string;
    private JTextArea textArea;
    private List<FileSetting> fileSettingList;

    public HighlightingThread(StringBuilder stringBuilder, JTextArea textArea) {
        this.string = stringBuilder.toString();
        this.textArea = textArea;
        fileSettingList = Lists.newArrayList(new FileSetting("hello", true, true, Color.RED),
                new FileSetting("test", true, true, Color.BLUE));
    }

    @Override
    public void run() {
        Iterable<String> strings = Splitter.on("\n").split(string);

        int i = 0;
        for (String string : strings) {
            for (FileSetting fileSetting : fileSettingList) {
                if (fileSetting.matches(string)) {
                    doHighlight(i, fileSetting.getColor());
                }
            }
            i++;
        }
    }

    private void doHighlight(int lineNumber, Color color) {
        try {
            int p0 = textArea.getLineStartOffset(lineNumber);
            int p1 = textArea.getLineEndOffset(lineNumber);

            textArea.getHighlighter().addHighlight(p0, p1, new DefaultHighlighter.DefaultHighlightPainter(color));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
