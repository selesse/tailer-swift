package com.selesse.tailerswift.gui.lines;

import com.selesse.tailerswift.gui.SmartScroller;
import com.selesse.tailerswift.gui.highlighting.FileSetting;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.util.List;

/**
 * The widget for a single watched file: owns the line buffer, incoming-chunk splitting,
 * and the scroll pane/gutter pair that render it. This is the composition root that
 * replaced a single {@code JTextPane} - everything it delegates to ({@link LineBuffer},
 * {@link LineSplitter}, {@link VirtualLinePane}, {@link LineNumberGutter}) is independently
 * testable; this class just wires them together and owns EDT confinement.
 *
 * <p>All mutation (appendChunk/setText/setFont) is marshalled onto the EDT, same as the
 * Document mutations it replaced - the difference is that the work being marshalled is
 * now O(new content) instead of O(whole buffer), so doing it on the EDT is no longer
 * the bottleneck it used to be.
 */
public class TailPane {
    private static final int DEFAULT_CAPACITY_LINES = 100_000;

    private final RingLineBuffer lineBuffer;
    private final LineSplitter splitter;
    private final VirtualLinePane linePane;
    private final LineNumberGutter gutter;
    private final JScrollPane scrollPane;

    public TailPane(List<FileSetting> fileSettings, Font font, DropTarget dropTarget) {
        this(fileSettings, font, dropTarget, DEFAULT_CAPACITY_LINES);
    }

    public TailPane(List<FileSetting> fileSettings, Font font, DropTarget dropTarget, int capacityLines) {
        this.lineBuffer = new RingLineBuffer(capacityLines);
        this.splitter = new LineSplitter();
        this.linePane = new VirtualLinePane(lineBuffer, fileSettings, font);
        this.gutter = new LineNumberGutter(lineBuffer, linePane);

        linePane.setBorder(new EmptyBorder(0, 5, 0, 0));
        if (dropTarget != null) {
            linePane.setDropTarget(dropTarget);
        }

        this.scrollPane = new JScrollPane(linePane);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new SmartScroller(scrollPane));
        scrollPane.setRowHeaderView(gutter);
    }

    /** Appends newly-read file content. Safe to call from any thread. */
    public void appendChunk(String chunk) {
        SwingUtilities.invokeLater(() -> appendChunkOnEdt(chunk));
    }

    /** Replaces all content, e.g. when a file is (re)created or deleted (pass ""). */
    public void setText(String fullText) {
        SwingUtilities.invokeLater(() -> {
            lineBuffer.clear();
            splitter.reset();
            linePane.reset();
            appendChunkOnEdt(fullText);
        });
    }

    private void appendChunkOnEdt(String chunk) {
        List<String> newLines = splitter.append(chunk);
        for (String line : newLines) {
            lineBuffer.append(line);
        }
        linePane.onNewLines(newLines);
        linePane.setPartialLine(splitter.pendingLine());
        gutter.updatePreferredSize();
        linePane.repaint();
        gutter.repaint();
    }

    /** The full text currently held, in the same shape a {@code JTextComponent#getText()} would return. */
    public String getText() {
        StringBuilder text = new StringBuilder();
        for (String line : lineBuffer.asList()) {
            text.append(line).append('\n');
        }
        text.append(splitter.pendingLine());
        return text.toString();
    }

    public void setFont(Font font) {
        linePane.setFont(font);
        gutter.setFont(font);
    }

    /** Repaints in place - used when the shared highlight settings change, since those are resolved at paint time. */
    public void repaintHighlights() {
        linePane.repaint();
    }

    public JComponent getComponent() {
        return scrollPane;
    }

    /** Gives the content pane keyboard focus, so Page Up/Down/Home/End/arrows work without a click first. */
    public void requestScrollFocus() {
        linePane.requestScrollFocus();
    }
}
