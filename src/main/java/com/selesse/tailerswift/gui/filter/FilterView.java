package com.selesse.tailerswift.gui.filter;

import com.selesse.tailerswift.gui.view.FeatureView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class FilterView extends FeatureView {
    private JTextArea textArea;

    public FilterView(final Filter filter) {
        this.panel = new JPanel();
        this.textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(800, 300));

        JLabel textFieldLabel = new JLabel("Filter query");
        final JTextField textField = new JTextField(20);

        panel.add(textFieldLabel);
        panel.add(textField);
        panel.add(scrollPane);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FilterResults results = filter.filter(textField.getText());

                Map<String, FilterMatches> stringFilterMatchesMap = results.getAllMatches();

                textArea.append("Results for filtering for \"" + textField.getText() + "\"\n");

                for (String file : stringFilterMatchesMap.keySet()) {
                    FilterMatches filterMatches = stringFilterMatchesMap.get(file);

                    textArea.append(filterMatches.getAllMatches().size() + " matches in file " + file + "\n");

                    for (int lineNumber : filterMatches.getAllMatches().keySet()) {
                        textArea.append("  line " + lineNumber + " : " + filterMatches.getMatch(lineNumber) + "\n");
                    }

                }
            }
        });
    }
}
