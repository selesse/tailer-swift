package com.selesse.tailerswift.gui.features;

import com.google.common.collect.Lists;
import com.selesse.tailerswift.gui.filter.FilterThread;
import com.selesse.tailerswift.gui.highlighting.FileSetting;
import com.selesse.tailerswift.gui.highlighting.HighlightThread;
import com.selesse.tailerswift.gui.search.SearchThread;

import javax.swing.text.JTextComponent;

public class FeatureFactory {
    public static void createAndRunThreads(StringBuilder stringBuilder, JTextComponent textComponent) {
        Thread highlightingThread = new Thread(new HighlightThread(textComponent, Lists.<FileSetting>newArrayList()));
        highlightingThread.start();

        Thread filterThread = new Thread(new FilterThread(stringBuilder, textComponent));
        filterThread.start();

        Thread searchThread = new Thread(new SearchThread(textComponent, stringBuilder.toString()));
        searchThread.start();
    }
}
