package com.selesse.tailerswift.ui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class HighlightingThread implements Runnable {
    private String string;
    private JTextComponent textComponent;
    private List<FileSetting> fileSettingList;

    public HighlightingThread(StringBuilder stringBuilder, JTextComponent textComponent) {
        this.string = stringBuilder.toString();
        this.textComponent = textComponent;
        fileSettingList = Lists.newArrayList(new FileSetting("hello", true, true,
                new HighlightSettings(Color.WHITE, Color.RED, false, false, true)),
                new FileSetting("test", true, true,
                        new HighlightSettings(Color.YELLOW, Color.MAGENTA, true, false, false)));
    }

    @Override
    public void run() {
        Iterable<String> strings = Splitter.on(System.lineSeparator()).split(string);

        int offset = 0;
        for (String string : strings) {
            for (FileSetting fileSetting : fileSettingList) {
                if (fileSetting.matches(string)) {
                    doHighlight(offset, string.length(), fileSetting.getHighlightSettings());
                }
            }
            offset += string.length() + System.lineSeparator().length();
        }
    }

    private void doHighlight(int offset, int highlightLength, HighlightSettings highlightSettings) {
        // make sure that the text component's Document is a StyledDocument
        Document document = textComponent.getDocument();
        StyledDocument styledDocument;
        if (!(document instanceof DefaultStyledDocument)) {
            document = new DefaultStyledDocument();
        }
        styledDocument = (StyledDocument) document;

        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, highlightSettings.getForegroundColor());
        StyleConstants.setBackground(attributeSet, highlightSettings.getBackgroundColor());
        StyleConstants.setBold(attributeSet, highlightSettings.isBold());
        StyleConstants.setItalic(attributeSet, highlightSettings.isItalic());
        StyleConstants.setUnderline(attributeSet, highlightSettings.isUnderline());
        styledDocument.setCharacterAttributes(offset, highlightLength, attributeSet, true);
    }
}
