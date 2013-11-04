package com.selesse.tailerswift.gui.features;

import com.selesse.tailerswift.gui.filter.FilterThread;
import com.selesse.tailerswift.gui.highlighting.HighlightThread;
import com.selesse.tailerswift.gui.search.SearchThread;

import javax.swing.text.JTextComponent;

public class FeatureFactory {
    public static void createAndRunThreads(StringBuilder stringBuilder, JTextComponent textComponent) {
        Thread highlightingThread = new Thread(new HighlightThread(stringBuilder, textComponent));
        highlightingThread.start();

        Thread filterThread = new Thread(new FilterThread(stringBuilder, textComponent));
        filterThread.start();

        Thread searchThread = new Thread(new SearchThread(stringBuilder));
        searchThread.start();
    }
}
