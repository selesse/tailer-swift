package com.selesse.tailerswift.gui.lines;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Line-number column for a {@link VirtualLinePane}, meant to be used as a
 * {@code JScrollPane} row header - Swing keeps a row header's vertical scroll position
 * in sync with the main viewport automatically, so this only has to paint whatever
 * range {@link VisibleLineRange} says is visible, the same as the pane it's next to.
 *
 * <p>Unlike the old {@code TextLineNumber}, this never calls {@code viewToModel}/
 * {@code modelToView} - with a fixed line height, pixel-to-line is O(1) arithmetic
 * instead of a Swing View-tree walk, which is what made the old gutter's repaint cost
 * scale with document size.
 */
public class LineNumberGutter extends JComponent {
    private static final int OVERSCAN_LINES = 5;
    private static final int BORDER_GAP_PX = 5;
    private static final Border OUTER_BORDER = new MatteBorder(0, 0, 0, 2, Color.GRAY);
    private static final int MIN_DISPLAY_DIGITS = 3;

    private final LineBuffer lineBuffer;
    private final VirtualLinePane linePane;

    private Font currentFont;
    private FontMetrics fontMetrics;
    private int lastDigits;

    public LineNumberGutter(LineBuffer lineBuffer, VirtualLinePane linePane) {
        this.lineBuffer = lineBuffer;
        this.linePane = linePane;
        setBorder(new CompoundBorder(OUTER_BORDER, new EmptyBorder(0, BORDER_GAP_PX, 0, BORDER_GAP_PX)));
        setFont(linePane.getFont());
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.currentFont = font;
        this.fontMetrics = getFontMetrics(font);
        lastDigits = 0;
        updatePreferredSize();
    }

    /** Call whenever the pane's own preferred size may have changed (new lines, font change). */
    public void updatePreferredSize() {
        if (fontMetrics == null) {
            return;
        }
        int highestLineNumber = lineBuffer.getFirstLineNumber() + lineBuffer.size();
        int digits = Math.max(String.valueOf(highestLineNumber).length(), MIN_DISPLAY_DIGITS);

        if (digits != lastDigits) {
            lastDigits = digits;
            java.awt.Insets insets = getInsets();
            int width = insets.left + insets.right + fontMetrics.charWidth('0') * digits;
            setPreferredSize(new Dimension(width, linePane.getPreferredSize().height));
            revalidate();
        }
        else {
            setPreferredSize(new Dimension(getPreferredSize().width, linePane.getPreferredSize().height));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        int lineHeight = linePane.getLineHeight();
        Rectangle clip = g.getClipBounds();
        VisibleLineRange range = VisibleLineRange.compute(clip.y, clip.height, lineHeight, lineBuffer.size(),
                OVERSCAN_LINES);
        if (range.isEmpty()) {
            return;
        }

        java.awt.Insets insets = getInsets();
        int availableWidth = getWidth() - insets.left - insets.right;

        g.setFont(currentFont);
        g.setColor(getForeground());

        int ascent = fontMetrics.getAscent();
        for (int i = range.firstInclusive(); i < range.lastExclusive(); i++) {
            String number = String.valueOf(lineBuffer.getFirstLineNumber() + i);
            int stringWidth = fontMetrics.stringWidth(number);
            int x = insets.left + (availableWidth - stringWidth);
            int y = i * lineHeight + ascent;
            g.drawString(number, x, y);
        }
    }
}
