package com.selesse.tailerswift.gui.filter;

import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

public class FilterMatches {
    public Map<Integer, String> match;

    public FilterMatches() {
        match = Maps.newTreeMap();
    }

    public void addMatch(int lineNumber, String matchingLine) {
        match.put(lineNumber, matchingLine);
    }

    public Map<Integer, String> getAllMatches() {
        return Collections.unmodifiableMap(match);
    }

    public String getMatch(int lineNumber) {
        return match.get(lineNumber);
    }
}
