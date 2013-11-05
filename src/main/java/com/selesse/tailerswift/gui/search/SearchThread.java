package com.selesse.tailerswift.gui.search;

import com.google.common.base.Splitter;

import javax.swing.text.JTextComponent;

public class SearchThread implements Runnable {
    private final String fileContents;
    private final String queryString;
    private SearchMatches searchMatches;

    public SearchThread(JTextComponent textComponent, String queryString) {
        this.fileContents = textComponent.getText();
        this.queryString = queryString;

        searchMatches = new SearchMatches();
    }

    @Override
    public void run() {
        // \r?\n is important: it makes this program line-ending agnostic
        Iterable<String> splitStrings = Splitter.onPattern("\r?\n").split(fileContents);

        int i = 1;
        for (String line : splitStrings) {
            if (line.contains(queryString)) {
                searchMatches.addMatch(i, line);
            }
            i++;
        }
    }

    public SearchMatches getResults() {
        return searchMatches;
    }
}
