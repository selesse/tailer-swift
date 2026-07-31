package com.selesse.tailerswift.gui.lines;

import com.selesse.tailerswift.gui.highlighting.FileSetting;
import com.selesse.tailerswift.gui.highlighting.HighlightSettings;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Renders a {@link LineBuffer} virtually: no matter how many lines the buffer holds,
 * only the lines intersecting the current clip rectangle are ever laid out or painted.
 * This is what makes scroll/append cost independent of file size - the old JTextPane
 * approach laid out (and re-highlighted) the whole backing Document on every change.
 *
 * <p>Lines are drawn unwrapped, at a fixed height, so that pixel-to-line and
 * line-to-pixel math stays O(1). Long lines run off the right edge and are reachable via
 * the horizontal scrollbar rather than being wrapped, which would require per-line
 * layout and defeat the fixed-line-height assumption this whole approach relies on.
 */
public class VirtualLinePane extends JComponent implements Scrollable {
    private static final int OVERSCAN_LINES = 5;
    private static final int LEFT_MARGIN_PX = 4;

    private final LineBuffer lineBuffer;
    private final List<FileSetting> fileSettings;

    private Font currentFont;
    private FontMetrics fontMetrics;
    private int lineHeight = 1;
    private int ascent;
    private int maxLineWidthPx;
    private String partialLine = "";

    public VirtualLinePane(LineBuffer lineBuffer, List<FileSetting> fileSettings, Font font) {
        this.lineBuffer = lineBuffer;
        this.fileSettings = fileSettings;
        setOpaque(true);
        setBackground(Color.WHITE);
        setFont(font);

        // A JTextPane gets keyboard scrolling and click-to-focus for free from Swing's
        // text key bindings; a bare JComponent needs it wired up explicitly.
        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
        installScrollKeyBindings();
    }

    private void installScrollKeyBindings() {
        InputMap inputMap = getInputMap(WHEN_FOCUSED);
        ActionMap actionMap = getActionMap();

        bindKey(inputMap, actionMap, KeyEvent.VK_UP, "scrollLineUp", () -> scrollByPixels(-lineHeight));
        bindKey(inputMap, actionMap, KeyEvent.VK_DOWN, "scrollLineDown", () -> scrollByPixels(lineHeight));
        bindKey(inputMap, actionMap, KeyEvent.VK_PAGE_UP, "scrollPageUp", () -> scrollByPixels(-getVisibleRect().height));
        bindKey(inputMap, actionMap, KeyEvent.VK_PAGE_DOWN, "scrollPageDown", () -> scrollByPixels(getVisibleRect().height));
        bindKey(inputMap, actionMap, KeyEvent.VK_HOME, "scrollToTop", this::scrollToTop);
        bindKey(inputMap, actionMap, KeyEvent.VK_END, "scrollToBottom", this::scrollToBottom);
    }

    private void bindKey(InputMap inputMap, ActionMap actionMap, int keyCode, String name, Runnable action) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, 0);
        inputMap.put(keyStroke, name);
        actionMap.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    private void scrollByPixels(int deltaY) {
        Rectangle visible = getVisibleRect();
        visible.y = Math.max(0, Math.min(getHeight() - visible.height, visible.y + deltaY));
        scrollRectToVisible(visible);
    }

    private void scrollToTop() {
        Rectangle visible = getVisibleRect();
        visible.y = 0;
        scrollRectToVisible(visible);
    }

    private void scrollToBottom() {
        Rectangle visible = getVisibleRect();
        visible.y = Math.max(0, getHeight() - visible.height);
        scrollRectToVisible(visible);
    }

    /** Gives this pane keyboard focus so scroll keys work without requiring a click first. */
    public void requestScrollFocus() {
        requestFocusInWindow();
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.currentFont = font;
        this.fontMetrics = getFontMetrics(font);
        if (fontMetrics != null) {
            this.lineHeight = Math.max(1, fontMetrics.getHeight());
            this.ascent = fontMetrics.getAscent();
        }
        updatePreferredSize();
    }

    public int getLineHeight() {
        return lineHeight;
    }

    /** Call after new lines are appended to the buffer, so the preferred size stays accurate. */
    public void onNewLines(List<String> newLines) {
        if (fontMetrics != null) {
            for (String line : newLines) {
                int width = fontMetrics.stringWidth(line);
                if (width > maxLineWidthPx) {
                    maxLineWidthPx = width;
                }
            }
        }
        updatePreferredSize();
    }

    /**
     * The tail end of the file since its last newline - not yet a complete line, but
     * historically shown live (e.g. progress indicators using bare {@code \r}, or a
     * process that hasn't flushed a trailing newline yet). Rendered as one extra line
     * past the end of the buffer.
     */
    public void setPartialLine(String partialLine) {
        this.partialLine = partialLine;
        if (fontMetrics != null && !partialLine.isEmpty()) {
            maxLineWidthPx = Math.max(maxLineWidthPx, fontMetrics.stringWidth(partialLine));
        }
        updatePreferredSize();
    }

    /** Call when the buffer has been cleared and replaced (e.g. file recreated). */
    public void reset() {
        maxLineWidthPx = 0;
        partialLine = "";
        updatePreferredSize();
    }

    private int totalLineCount() {
        return lineBuffer.size() + (partialLine.isEmpty() ? 0 : 1);
    }

    private void updatePreferredSize() {
        int height = totalLineCount() * lineHeight;
        int width = maxLineWidthPx + LEFT_MARGIN_PX * 2;
        setPreferredSize(new Dimension(Math.max(width, 1), Math.max(height, 1)));
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        Rectangle clip = g.getClipBounds();
        VisibleLineRange range = VisibleLineRange.compute(clip.y, clip.height, lineHeight, totalLineCount(),
                OVERSCAN_LINES);
        if (range.isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (int i = range.firstInclusive(); i < range.lastExclusive(); i++) {
            paintLine(g2, i);
        }
    }

    private void paintLine(Graphics2D g2, int index) {
        String line = index < lineBuffer.size() ? lineBuffer.get(index) : partialLine;
        int y = index * lineHeight;

        HighlightSettings highlight = LineHighlighter.resolve(line, fileSettings);

        if (highlight != null && highlight.getBackgroundColor() != null) {
            g2.setColor(highlight.getBackgroundColor());
            g2.fillRect(0, y, getWidth(), lineHeight);
        }

        g2.setColor(highlight != null && highlight.getForegroundColor() != null
                ? highlight.getForegroundColor() : getForeground());
        g2.setFont(styledFont(highlight));

        int baselineY = y + ascent;
        g2.drawString(line, LEFT_MARGIN_PX, baselineY);

        if (highlight != null && highlight.isUnderline()) {
            int width = g2.getFontMetrics().stringWidth(line);
            g2.drawLine(LEFT_MARGIN_PX, baselineY + 1, LEFT_MARGIN_PX + width, baselineY + 1);
        }
    }

    private Font styledFont(HighlightSettings highlight) {
        if (highlight == null) {
            return currentFont;
        }
        int style = Font.PLAIN;
        if (highlight.isBold()) {
            style |= Font.BOLD;
        }
        if (highlight.isItalic()) {
            style |= Font.ITALIC;
        }
        return style == Font.PLAIN ? currentFont : currentFont.deriveFont(style);
    }

    // -- Scrollable --

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return lineHeight;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return orientation == javax.swing.SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
