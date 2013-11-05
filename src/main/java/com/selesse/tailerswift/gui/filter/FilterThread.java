package com.selesse.tailerswift.gui.filter;

import com.google.common.base.Splitter;

import javax.swing.text.JTextComponent;

public class FilterThread implements Runnable {
    private final String fileContents;
    private final String queryString;
    private FilterMatches filterMatches;

    public FilterThread(JTextComponent textComponent, String queryString) {
        this.fileContents = textComponent.getText();
        this.queryString = queryString;

        filterMatches = new FilterMatches();
    }

    @Override
    public void run() {
        // \r?\n is important: it makes this program line-ending agnostic
        Iterable<String> splitStrings = Splitter.onPattern("\r?\n").split(fileContents);

        int i = 1;
        for (String line : splitStrings) {
            if (line.contains(queryString)) {
                filterMatches.addMatch(i, line);
            }
            i++;
        }
    }

    public FilterMatches getResults() {
        return filterMatches;
    }
}
