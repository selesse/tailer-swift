package com.selesse.tailerswift.gui;

import org.fest.swing.exception.ComponentLookupException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FeatureTester extends AbstractMainFrameTester {
    /**
     * Basic steps:
     *   1. Assert that "nameView" doesn't exist
     *   2. Click on "name"
     *   3. Assert that "nameView" is visible
     *   4. Click on "name"
     *   5. Assert that "nameView" doesn't exist anymore
     *   6. Click on "name"
     *   7. Assert that "nameView" is visible
     *
     */
    private void testClickingButtonTogglesPanelVisibility(String name) {
        boolean exceptionThrown = false;
        try {
            window.button(name + "View");
        }
        catch (ComponentLookupException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        exceptionThrown = false;
        window.button(name).click();
        window.panel(name + "View").requireVisible();
        window.button(name).click();
        try {
            window.panel(name + "View");
        }
        catch (ComponentLookupException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        window.button(name).click();
        window.panel(name + "View").requireVisible();
    }

    @Test
    public void testClickingFilterButtonTogglesItsVisibility() {
        testClickingButtonTogglesPanelVisibility("Filter");
    }

    @Test
    public void testClickingHighlightButtonTogglesItsVisibility() {
        testClickingButtonTogglesPanelVisibility("Highlight");
    }

    @Test
    public void testClickingSearchButtonTogglesItsVisibility() {
        testClickingButtonTogglesPanelVisibility("Search");
    }

    @Test
    public void testClickingFeaturesInRightOrderMakesSense() {
        // Clicking on "Filter" should pop up the FilterView
        window.button("Filter").click();
        window.panel("FilterView").requireVisible();

        // Clicking on "Highlight" should pop up HighlightView, and hide FilterView
        window.button("Highlight").click();
        window.panel("HighlightView").requireVisible();
        boolean exceptionThrown = false;
        try {
            window.button("FilterView");
        }
        catch (ComponentLookupException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        exceptionThrown = false;

        // Clicking on "Search" should pop up SearchView, and hide HighlightView
        window.button("Search").click();
        window.panel("SearchView").requireVisible();
        try {
            window.button("HighlightView");
        }
        catch (ComponentLookupException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        exceptionThrown = false;

        // Clicking on search again should SearchView invisible
        window.button("Search").click();
        try {
            window.button("SearchView");
        }
        catch (ComponentLookupException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}
