package com.selesse.tailerswift.gui.search;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class SearchView {
    private JPanel panel;
    private JTextArea textArea;

    public SearchView(final Search search) {
        this.panel = new JPanel();
        this.textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(800, 300));

        JLabel textFieldLabel = new JLabel("Search query");
        final JTextField textField = new JTextField(20);

        panel.add(textFieldLabel);
        panel.add(textField);
        panel.add(scrollPane);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchResults results = search.searchFor(textField.getText());

                Map<String, SearchMatches> stringSearchMatchesMap = results.getAllMatches();

                textArea.append("Results for searching for \"" + textField.getText() + "\"\n");

                for (String file : stringSearchMatchesMap.keySet()) {
                    SearchMatches searchMatches = stringSearchMatchesMap.get(file);

                    textArea.append(searchMatches.getAllMatches().size() + " matches in file " + file + "\n");

                    for (int lineNumber : searchMatches.getAllMatches().keySet()) {
                        textArea.append("  line " + lineNumber + " : " + searchMatches.getMatch(lineNumber) + "\n");
                    }

                }
            }
        });
    }

    public JComponent getComponent() {
        return panel;
    }
}
