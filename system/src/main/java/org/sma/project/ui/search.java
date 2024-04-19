package org.sma.project.ui;

import javax.swing.*;
import java.awt.*;

import jade.gui.GuiEvent;
import org.sma.project.agents.ResourceAgent;
import org.sma.project.agents.UserAgent;

public class search {
    private UserAgent userAgent;
    private ResourceAgent resourceAgent;
    private JPanel panel;
    private JTextField SearchQueryInput;
    private JButton SearchButton;
    private JList<String> ResultsList;

    public void show() {
        JFrame frame = new JFrame("multi-agent-system for information retrieval");
        frame.setContentPane(new search(userAgent, resourceAgent).panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1280, 720));
        frame.pack();
        frame.setVisible(true);
    }

    public search(UserAgent userAgent, ResourceAgent resourceAgent) {
        this.userAgent = userAgent;
        this.resourceAgent = resourceAgent;
        SearchButton.addActionListener(this::searchButtonActionPerformed);
        resourceAgent.setDataFoundListener(data -> {
            DefaultListModel<String> model = (DefaultListModel<String>) ResultsList.getModel();
            model.removeAllElements();
            model.addElement(data);
        });
    }

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String searchQuery = SearchQueryInput.getText();

        if (searchQuery.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a search query.");
            return;
        }

        GuiEvent ev = new GuiEvent(this, 1);
        System.out.println("search query: " + searchQuery);
        System.out.println(ev);

        if (ev != null && userAgent != null) {
            ev.addParameter(searchQuery);
            userAgent.onGuiEvent(ev);
        }
    }
}
