package com.selesse.tailerswift.gui.section;

import org.junit.Test;

import javax.swing.JLabel;
import java.awt.Component;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Covers the show/hide/switch-between-panels behavior that used to require a real window
 * and FEST/AssertJ-Swing driving actual button clicks (see the old FeatureTester). Feature,
 * FeaturePanel, and ButtonActionListener only manipulate lightweight Swing components
 * (JPanel/JLabel) without needing a display, so this exercises the real production classes
 * directly - no window, no click automation, no platform-specific skip logic.
 */
public class ButtonActionListenerTester {
    private static Feature newFeature(String name) {
        return new Feature(new FeatureContent() {
            @Override
            public String getFeatureName() {
                return name;
            }

            @Override
            public Component getViewComponent() {
                return new JLabel(name);
            }
        });
    }

    @Test
    public void clickingAHiddenFeatureShowsItAndMakesItTheActiveOne() {
        FeaturePanel featurePanel = new FeaturePanel();
        Feature feature = newFeature("Search");
        ButtonActionListener listener = new ButtonActionListener(featurePanel, feature);

        listener.actionPerformed(null);

        assertTrue(feature.getVisibility());
        assertSame(feature, featurePanel.getFeature());
    }

    @Test
    public void clickingTheActiveFeatureAgainHidesIt() {
        FeaturePanel featurePanel = new FeaturePanel();
        Feature feature = newFeature("Search");
        ButtonActionListener listener = new ButtonActionListener(featurePanel, feature);

        listener.actionPerformed(null);
        listener.actionPerformed(null);

        assertFalse(feature.getVisibility());
    }

    @Test
    public void selectingADifferentFeatureReplacesThePreviousOneInThePanel() {
        FeaturePanel featurePanel = new FeaturePanel();
        Feature filter = newFeature("Filter");
        Feature highlight = newFeature("Highlight");
        ButtonActionListener filterListener = new ButtonActionListener(featurePanel, filter);
        ButtonActionListener highlightListener = new ButtonActionListener(featurePanel, highlight);

        filterListener.actionPerformed(null);
        assertSame(filter, featurePanel.getFeature());
        assertTrue(Arrays.asList(featurePanel.getComponents()).contains(filter.getComponent()));

        highlightListener.actionPerformed(null);

        assertSame(highlight, featurePanel.getFeature());
        assertTrue(Arrays.asList(featurePanel.getComponents()).contains(highlight.getComponent()));
        assertFalse(Arrays.asList(featurePanel.getComponents()).contains(filter.getComponent()));
    }

    @Test
    public void switchingBackToAPreviouslyActiveFeatureShowsItAgain() {
        FeaturePanel featurePanel = new FeaturePanel();
        Feature search = newFeature("Search");
        Feature highlight = newFeature("Highlight");
        ButtonActionListener searchListener = new ButtonActionListener(featurePanel, search);
        ButtonActionListener highlightListener = new ButtonActionListener(featurePanel, highlight);

        searchListener.actionPerformed(null);
        highlightListener.actionPerformed(null);
        searchListener.actionPerformed(null);

        assertSame(search, featurePanel.getFeature());
        assertTrue(Arrays.asList(featurePanel.getComponents()).contains(search.getComponent()));
    }
}
