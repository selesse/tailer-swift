package com.selesse.tailerswift.gui.lines;

import com.selesse.tailerswift.gui.highlighting.FileSetting;
import com.selesse.tailerswift.gui.highlighting.HighlightSettings;

import java.util.List;

/**
 * Resolves which {@link HighlightSettings} (if any) apply to a single line.
 *
 * <p>This used to be done by re-scanning the entire buffer on every append (see
 * the old {@code HighlightThread}), which is why highlighting got slower the longer
 * a file ran. Because rendering is now virtualized - only the handful of visible
 * lines are painted at a time - resolving highlights per-line, at paint time, costs
 * O(visible lines x settings) instead of O(whole buffer x settings) and needs no cache
 * or invalidation when settings change.
 */
public class LineHighlighter {
    /**
     * When multiple settings match the same line, the last match in the list wins -
     * this matches the historical behavior of the old highlighter.
     */
    public static HighlightSettings resolve(String line, List<FileSetting> fileSettings) {
        HighlightSettings match = null;
        for (FileSetting fileSetting : fileSettings) {
            if (fileSetting.matchesHighlight(line)) {
                match = fileSetting.getHighlightSettings();
            }
        }
        return match;
    }
}
