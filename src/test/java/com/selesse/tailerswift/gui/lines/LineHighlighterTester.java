package com.selesse.tailerswift.gui.lines;

import com.google.common.collect.Lists;
import com.selesse.tailerswift.gui.highlighting.FileSetting;
import com.selesse.tailerswift.gui.highlighting.HighlightSettings;
import org.junit.Test;

import java.awt.Color;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class LineHighlighterTester {
    private static final String FILE = "/var/log/app.log";

    private static FileSetting settingFor(String pattern, HighlightSettings settings) {
        return new FileSetting(pattern, false, true, settings, FILE);
    }

    @Test
    public void noSettingsMeansNoHighlight() {
        HighlightSettings result = LineHighlighter.resolve("ERROR something broke", Lists.newArrayList());

        assertNull(result);
    }

    @Test
    public void nonMatchingSettingIsIgnored() {
        HighlightSettings settings = new HighlightSettings(Color.RED, Color.BLACK, false, false, false);
        List<FileSetting> fileSettings = Lists.newArrayList(settingFor("WARN", settings));

        HighlightSettings result = LineHighlighter.resolve("ERROR something broke", fileSettings);

        assertNull(result);
    }

    @Test
    public void matchingSettingIsReturned() {
        HighlightSettings settings = new HighlightSettings(Color.RED, Color.BLACK, false, false, false);
        List<FileSetting> fileSettings = Lists.newArrayList(settingFor("ERROR", settings));

        HighlightSettings result = LineHighlighter.resolve("ERROR something broke", fileSettings);

        assertSame(settings, result);
    }

    @Test
    public void lastMatchingSettingWinsWhenSeveralMatch() {
        HighlightSettings first = new HighlightSettings(Color.RED, Color.BLACK, false, false, false);
        HighlightSettings second = new HighlightSettings(Color.YELLOW, Color.BLACK, false, false, false);
        List<FileSetting> fileSettings = Lists.newArrayList(
                settingFor("ERROR", first),
                settingFor("something", second));

        HighlightSettings result = LineHighlighter.resolve("ERROR something broke", fileSettings);

        assertSame(second, result);
    }

    @Test
    public void blankLinesAreNeverHighlighted() {
        HighlightSettings settings = new HighlightSettings(Color.RED, Color.BLACK, false, false, false);
        List<FileSetting> fileSettings = Lists.newArrayList(settingFor("", settings));

        HighlightSettings result = LineHighlighter.resolve("   ", fileSettings);

        assertNull(result);
    }

    @Test
    public void equalsIgnoreCaseModeMatchesRegardlessOfCase() {
        HighlightSettings settings = new HighlightSettings(Color.RED, Color.BLACK, false, false, false);
        FileSetting fileSetting = new FileSetting("error", true, false, settings, FILE);

        assertSame(settings, LineHighlighter.resolve("ERROR", Lists.newArrayList(fileSetting)));
        assertNull(LineHighlighter.resolve("ERROR extra text", Lists.newArrayList(fileSetting)));
    }
}
