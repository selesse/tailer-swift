package com.selesse.tailerswift.gui.filter;

import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

public class FilterResults {
    private Map<String, FilterMatches> fileFilterMatchesMap;

    public FilterResults() {
        fileFilterMatchesMap = Maps.newHashMap();
    }

    public void addMatch(String absoluteFilePath, FilterMatches filterMatches) {
        fileFilterMatchesMap.put(absoluteFilePath, filterMatches);
    }

    public Map<String, FilterMatches> getAllMatches() {
        return Collections.unmodifiableMap(fileFilterMatchesMap);
    }
}

