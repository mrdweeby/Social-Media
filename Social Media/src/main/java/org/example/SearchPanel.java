package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class SearchPanel extends JPanel {
    private User loggedInUser;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JPanel profilePanelContainer;
    private HomeScreen homeScreen;

    public SearchPanel(User loggedInUser, HomeScreen homeScreen) {
        this.loggedInUser = loggedInUser;
        setLayout(new BorderLayout());
        this.homeScreen = homeScreen;
        // user list panel
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.addMouseListener(new UserSelectionListener());

        JScrollPane userListScrollPane = new JScrollPane(userList);
        userListScrollPane.setPreferredSize(new Dimension(200, 0));
        add(userListScrollPane, BorderLayout.WEST);

        // profile panel container
        profilePanelContainer = new JPanel(new BorderLayout());
        add(profilePanelContainer, BorderLayout.CENTER);

        // load all users
        loadAllUsers();
    }

    private void loadAllUsers() {
        userListModel.clear();
        for (User user : SocialNetwork.getAllUsers()) {
            if (!user.getUsername().equals(loggedInUser.getUsername())) {
                userListModel.addElement(user.getUsername());
            }
        }
    }

    private class UserSelectionListener extends MouseAdapter { // we created this method because we wanted to let user selects their group only by clicking.
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
                String selectedUsername = userList.getSelectedValue();
                if (selectedUsername != null) {
                    User selectedUser = SocialNetwork.getUser(selectedUsername);
                    showUserProfile(selectedUser);
                }
            }
        }
    }

    private void showUserProfile(User user) { // shows the searched user's profile information in a panel.
        profilePanelContainer.removeAll();
        profilePanelContainer.add(new UserProfilePanel(user), BorderLayout.CENTER);
        profilePanelContainer.revalidate();
        profilePanelContainer.repaint();
    }

    private class UserProfilePanel extends ProfilePanel {
        private JButton addFriendButton;

        public UserProfilePanel(User user) {
            super(user);
            addFriendButton = new JButton("Add Friend");
            addFriendButton.addActionListener(e -> {
                try {
                    addFriend(user);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            // add the button to the bottom of the profile panel
            add(addFriendButton, BorderLayout.SOUTH);
        }

        private void addFriend(User newFriend) throws IOException {
            if (!loggedInUser.getFriends().containsKey(newFriend.getUsername())) {
                loggedInUser.addFriend(newFriend);
                try {
                    SocialNetwork.getInstance().updateUsers();
                    homeScreen.updateWall();
                    JOptionPane.showMessageDialog(UserProfilePanel.this, "Friend added successfully.");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(UserProfilePanel.this, "Error adding friend: " + e.getMessage());
                }
            }else{
                JOptionPane.showMessageDialog(UserProfilePanel.this, "Friend already exists.");
            }
        }
    }
}