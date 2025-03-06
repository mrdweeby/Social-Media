package org.example;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FriendsSelectionDialog extends JDialog {
    private JList<String> friendsList;
    private DefaultListModel<String> listModel;
    private JButton addButton;
    private List<String> selectedFriendsString;
    private List<User> selectedFriends;

    public FriendsSelectionDialog(Frame owner, List<User> friends) { // this method provides selection of multiple button clicks
        super(owner, "Select Friends", true);
        setSize(400, 300);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        for (User friend : friends) {
            listModel.addElement(friend.getUsername());
        }

        friendsList = new JList<>(listModel);
        friendsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(new JScrollPane(friendsList), BorderLayout.CENTER);

        addButton = new JButton("Add to Group");
        addButton.addActionListener(e -> {
            selectedFriendsString = friendsList.getSelectedValuesList();
            selectedFriends = new ArrayList<>();
            selectedUsers(friends);
            dispose();
        });

        add(addButton, BorderLayout.SOUTH);
    }

    private void selectedUsers(List<User> friends){
        for (User friend : friends) {
            if (selectedFriendsString.contains(friend.getUsername())) {
                selectedFriends.add(friend);
            }
        }
    }

    public List<User> getSelectedFriends() {
        return selectedFriends;
    }
}