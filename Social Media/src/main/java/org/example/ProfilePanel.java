package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ProfilePanel extends JPanel {
    private User loggedInUser;
    private JTextArea messagesArea;
    private JTextArea friendsArea;
    private JTextArea groupsArea;

    public ProfilePanel(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        setLayout(new BorderLayout());

        // user's username
        JLabel usernameLabel = new JLabel("Username: " + loggedInUser.getUsername());
        add(usernameLabel, BorderLayout.NORTH);

        // panels for messages, friends, and groups
        JPanel centerPanel = new JPanel(new GridLayout(1, 3));

        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        centerPanel.add(createTitledPanel("Messages", messagesArea));

        friendsArea = new JTextArea();
        friendsArea.setEditable(false);
        centerPanel.add(createTitledPanel("Friends", friendsArea));

        groupsArea = new JTextArea();
        groupsArea.setEditable(false);
        centerPanel.add(createTitledPanel("Groups", groupsArea));

        add(centerPanel, BorderLayout.CENTER);

        // populate areas with user's data
        populateMessages();
        populateFriends();
        populateGroups();
    }

    private JPanel createTitledPanel(String title, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void populateMessages() {
        for (String message : loggedInUser.getWallMessages()) {
            messagesArea.append(message + "\n");
        }
    }

    private void populateFriends() {
        for (String friendUsername : loggedInUser.getFriends().keySet()) {
            friendsArea.append(friendUsername + "\n");
        }
    }

    private void populateGroups() {
        for (String groupName : loggedInUser.getGroups().keySet()) {
            groupsArea.append(groupName + "\n");
        }
    }
}