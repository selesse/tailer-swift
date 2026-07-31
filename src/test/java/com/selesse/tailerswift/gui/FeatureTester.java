package com.selesse.tailerswift.gui;

import org.junit.Test;

/**
 * The actual show/hide/switch-between-panels logic lives in
 * {@link com.selesse.tailerswift.gui.section.Feature},
 * {@link com.selesse.tailerswift.gui.section.FeaturePanel}, and
 * {@link com.selesse.tailerswift.gui.section.ButtonActionListener}, and is covered without a
 * window by {@code ButtonActionListenerTester}. This just checks that a real button click in
 * the real running app actually reaches that logic.
 */
public class FeatureTester extends AbstractMainFrameTester {
    @Test
    public void testClickingSearchButtonShowsSearchPanel() {
        window.button("Search").click();
        window.panel("SearchView").requireVisible();
    }
}
