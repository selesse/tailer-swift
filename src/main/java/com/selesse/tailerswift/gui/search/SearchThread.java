package com.selesse.tailerswift.gui.search;

import com.google.common.base.Splitter;

import javax.swing.text.JTextComponent;

public class SearchThread implements Runnable {
    private final String fileContents;
    private final String queryString;
    private SearchMatches searchMatches;
    private boolean finished;

    public SearchThread(JTextComponent textComponent, String queryString) {
        this.fileContents = textComponent.getText();
        this.queryString = queryString;

        searchMatches = new SearchMatches();
        finished = false;
    }

    @Override
    public void run() {
        int i = 1;
        // TODO line being \n is making me uneasy

        Iterable<String> splitStrings = Splitter.onPattern("\r?\n").split(fileContents);

        for (String line : splitStrings) {
            if (line.contains(queryString)) {
                searchMatches.addMatch(i, line);
            }
            i++;
        }

        finished = true;
    }

    public boolean isFinished() {
        return finished;
    }

    public SearchMatches getResults() {
        return searchMatches;
    }

}
