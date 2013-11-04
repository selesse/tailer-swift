package com.selesse.tailerswift.gui.search;

import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

public class SearchResults {
    private Map<String, SearchMatches> searchResultMap;

    public SearchResults() {
        searchResultMap = Maps.newHashMap();
    }

    public void addMatch(String absoluteFilePath, SearchMatches searchMatches) {
        searchResultMap.put(absoluteFilePath, searchMatches);
    }

    public Map<String, SearchMatches> getAllMatches() {
        return Collections.unmodifiableMap(searchResultMap);
    }
}
