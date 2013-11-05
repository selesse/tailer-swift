package com.selesse.tailerswift.gui.section;


import com.selesse.tailerswift.gui.MainFrame;

import java.awt.*;

public abstract class AbstractFeature implements FeatureContent {
    protected MainFrame mainFrame;
    protected String featureName;

    public AbstractFeature(String featureName, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.featureName = featureName;
    }

    public abstract Component getViewComponent();
    public String getFeatureName() {
        return featureName;
    }
}
