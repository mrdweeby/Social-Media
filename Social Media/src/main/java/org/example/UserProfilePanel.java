package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class UserProfilePanel extends ProfilePanel { // when you search for a user, it displays the user's information.
    private JButton addFriendButton;
    private User loggedInUser;
    private MessagesPanel messagesPanel;

    public UserProfilePanel(User user, User loggedInUser, MessagesPanel messagesPanel) {
        super(user);
        this.loggedInUser = loggedInUser;
        this.messagesPanel = messagesPanel;

        addFriendButton = new JButton("Add Friend");
        addFriendButton.addActionListener(e -> {
            try {
                addFriend(user);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(addFriendButton, BorderLayout.SOUTH);
    }

    private void addFriend(User newFriend) throws IOException {
        if (!loggedInUser.getFriends().containsKey(newFriend.getUsername())) {
            SocialMediaFacade smf = new SocialMediaFacade();
            smf.addFriend(loggedInUser,newFriend);
            try {
                SocialNetwork.getInstance().updateUsers();
                messagesPanel.updateFriendsList();
                JOptionPane.showMessageDialog(this, "Friend added successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error adding friend: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Friend already exists.");
        }
    }
}