package com.selesse.tailerswift.gui.highlighting;

import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.*;
import java.util.List;

public class HighlightThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(HighlightThread.class);
    private String currentText;
    private JTextComponent textComponent;
    private List<FileSetting> fileSettingList;

    public HighlightThread(JTextComponent textComponent, List<FileSetting> fileSettingsList) {
        this.currentText = textComponent.getText();
        this.textComponent = textComponent;
        this.fileSettingList = fileSettingsList;
    }

    @Override
    public void run() {
        // \r?\n is important: it makes this program line-ending agnostic
        Iterable<String> strings = Splitter.onPattern("\r?\n").split(currentText);

        int offset = 0, numberOfMatches = 0;
        for (String string : strings) {
            for (FileSetting fileSetting : fileSettingList) {
                if (fileSetting.matchesHighlight(string)) {
                    doHighlight(offset, string.length(), fileSetting.getHighlightSettings());
                    numberOfMatches++;
                }
            }
            offset += string.length() + 1; // Assume "\n", which is length 1
        }

        logger.info("Finished highlighting, found {} matches, file was {} bytes long", numberOfMatches, offset);
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
