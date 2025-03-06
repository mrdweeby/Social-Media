package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupPanel extends JPanel {
    private User loggedInUser;
    private JList<String> groupList;
    private JTextArea groupMessagesArea;
    private JList<String> groupMembersList;
    private JButton createGroupButton;
    private JButton addMemberButton;
    private DefaultListModel<String> groupListModel;
    private DefaultListModel<String> groupMembersListModel;

    public GroupPanel(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        setLayout(new BorderLayout());

        // group list panel
        JPanel groupListPanel = new JPanel(new BorderLayout());
        groupListModel = new DefaultListModel<>();
        groupList = new JList<>(groupListModel);
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupList.addListSelectionListener(e -> loadGroupDetails(groupList.getSelectedValue()));
        JScrollPane groupListScrollPane = new JScrollPane(groupList);
        groupListPanel.add(new JLabel("Groups"), BorderLayout.NORTH);
        groupListPanel.add(groupListScrollPane, BorderLayout.CENTER);
        add(groupListPanel, BorderLayout.WEST);

        // group messages panel
        JPanel groupMessagesPanel = new JPanel(new BorderLayout());
        groupMessagesArea = new JTextArea();
        groupMessagesArea.setEditable(false);
        JScrollPane groupMessagesScrollPane = new JScrollPane(groupMessagesArea);
        groupMessagesPanel.add(new JLabel("Group Messages"), BorderLayout.NORTH);
        groupMessagesPanel.add(groupMessagesScrollPane, BorderLayout.CENTER);

        JTextField newMessageField = new JTextField();
        JButton sendMessageButton = new JButton("Send");
        sendMessageButton.addActionListener(e -> {
            try {
                sendMessage(newMessageField.getText());
                newMessageField.setText("");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        JPanel newMessagePanel = new JPanel(new BorderLayout());
        newMessagePanel.add(newMessageField, BorderLayout.CENTER);
        newMessagePanel.add(sendMessageButton, BorderLayout.EAST);
        groupMessagesPanel.add(newMessagePanel, BorderLayout.SOUTH);
        add(groupMessagesPanel, BorderLayout.CENTER);

        // group members panel
        JPanel groupMembersPanel = new JPanel(new BorderLayout());
        groupMembersListModel = new DefaultListModel<>();
        groupMembersList = new JList<>(groupMembersListModel);
        JScrollPane groupMembersScrollPane = new JScrollPane(groupMembersList);
        groupMembersPanel.add(new JLabel("Group Members"), BorderLayout.NORTH);
        groupMembersPanel.add(groupMembersScrollPane, BorderLayout.CENTER);

        // add member button
        addMemberButton = new JButton("Add Member");
        addMemberButton.addActionListener(e -> {
            try {
                addMember();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        groupMembersPanel.add(addMemberButton, BorderLayout.SOUTH);

        add(groupMembersPanel, BorderLayout.EAST);

        // create group button
        createGroupButton = new JButton("Create Group");
        createGroupButton.addActionListener(e -> createGroup());
        add(createGroupButton, BorderLayout.SOUTH);

        // load user's groups
        loadUserGroups();
    }

    private void loadUserGroups() {
        groupListModel.clear();
        for (String groupName : loggedInUser.getGroups().keySet()) {
            groupListModel.addElement(groupName);
        }
    }

    private void loadGroupDetails(String groupName) {
        if (groupName == null || groupName.isEmpty()) return;

        Group group = SocialNetwork.getGroup(groupName);
        if (group == null) return;

        groupMessagesArea.setText("");
        groupMembersListModel.clear();

        // load group messages
        List<String> messages = group.getPosts();
        for (String message : messages) {
            groupMessagesArea.append(message);
        }

        // load group members
        for (User member : group.getMembers().values()) {
            groupMembersListModel.addElement(member.getUsername());
        }
    }

    private void sendMessage(String message) throws IOException {
        String groupName = groupList.getSelectedValue();
        if (groupName == null || groupName.isEmpty() || message.isEmpty()) return;

        Group group = SocialNetwork.getGroup(groupName);
        if (group == null) return;

        group.addPost(loggedInUser, message);
        group.updateDB();
        loadGroupDetails(groupName);
    }

    private void createGroup() {
        String groupName = JOptionPane.showInputDialog(this, "Enter group name:");
        if (groupName == null || groupName.isEmpty()) return;

        try {
            if (!SocialNetwork.groupExists(groupName)) {
                Group newGroup = new Group(groupName);
                newGroup.addMember(loggedInUser);
                SocialNetwork.getGroups().put(groupName, newGroup);
                loggedInUser.getGroups().put(groupName, newGroup); //add group to the user's group list
                loadUserGroups(); //refresh the group list
                loggedInUser.updateDB();
                JOptionPane.showMessageDialog(this, "Group created successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Group already exists.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating group: " + e.getMessage());
        }
    }

    private void addMember() throws IOException {
        String groupName = groupList.getSelectedValue();
        if (groupName == null || groupName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a group first.");
            return;
        }

        Group group = SocialNetwork.getGroup(groupName);
        if (group == null) {
            JOptionPane.showMessageDialog(this, "Group does not exist.");
            return;
        }

        List<User> friends = loggedInUser.getFriends().values().stream().toList();
        if (friends == null || friends.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no friends to add.");
            return;
        }

        List<User> friendCanBeAdded = new ArrayList<>();
        for (User friend : friends) {
            if (friend != null && !group.getMembers().containsKey(friend.getUsername())) {
                friendCanBeAdded.add(friend);
            }
        }

        if (friendCanBeAdded.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available friends to add to this group.");
            return;
        }

        FriendsSelectionDialog dialog = new FriendsSelectionDialog((Frame) SwingUtilities.getWindowAncestor(this), friendCanBeAdded);
        dialog.setVisible(true);

        List<User> selectedFriends = dialog.getSelectedFriends();
        if (selectedFriends == null || selectedFriends.isEmpty()) return;

        for (User friend : selectedFriends) {
            if (group.getMembers().containsKey(friend.getUsername())) {
                JOptionPane.showMessageDialog(this, "User " + friend.getUsername() + " is already a member of this group.");
                continue;
            }
            group.addMember(friend);
            try {
                group.updateDB();
                friend.updateDB();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error adding member: " + e.getMessage());
            }
        }

        loadGroupDetails(groupName); //refresh group details
        JOptionPane.showMessageDialog(this, "Members added successfully.");
    }
}