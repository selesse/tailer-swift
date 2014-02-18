package com.selesse.tailerswift.gui.search;

import com.selesse.tailerswift.gui.view.FeatureView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class SearchView extends FeatureView {
    private JTextArea textArea;

    public SearchView(final Search search) {
        this.panel = new JPanel();
        this.panel.setName("SearchView");
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

                for (Map.Entry<String, SearchMatches> entry : stringSearchMatchesMap.entrySet()) {
                    SearchMatches searchMatches = entry.getValue();
                    textArea.append(searchMatches.getAllMatches().size() + " matches in file " + entry.getKey() + "\n");

                    for (int lineNumber : searchMatches.getAllMatches().keySet()) {
                        textArea.append("  line " + lineNumber + " : " + searchMatches.getMatch(lineNumber) + "\n");
                    }

                }
            }
        });
    }
}
