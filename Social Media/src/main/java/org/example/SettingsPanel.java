package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsPanel extends JPanel {
    private User loggedInUser;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox canBeSearchableCheckBox;
    private JButton changeUsernameButton;
    private JButton changePasswordButton;
    private JButton changeCanBeSearchableButton;
    private JButton logoutButton;
    private SocialNetwork socialNetwork;

    public SettingsPanel(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.socialNetwork = SocialNetwork.getInstance();
        setLayout(new GridLayout(7, 2, 10, 10)); // Changed grid layout to accommodate the logout button

        // username change
        add(new JLabel("Change Username:"));
        usernameField = new JTextField(loggedInUser.getUsername());
        add(usernameField);

        changeUsernameButton = new JButton("Change Username");
        changeUsernameButton.addActionListener(e -> changeUsername());
        add(changeUsernameButton);

        // password change
        add(new JLabel("Change Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> changePassword());
        add(changePasswordButton);

        // searchable information change
        add(new JLabel("Can Be Searchable:"));
        canBeSearchableCheckBox = new JCheckBox("", loggedInUser.isCanBeSearchable());
        add(canBeSearchableCheckBox);

        changeCanBeSearchableButton = new JButton("Change Searchable Status");
        changeCanBeSearchableButton.addActionListener(e -> changeCanBeSearchable());
        add(changeCanBeSearchableButton);

        // logout button
        add(new JLabel("")); // Empty label for alignment
        logoutButton = new JButton("Log Out");
        logoutButton.addActionListener(e -> logout());
        add(logoutButton);

        // adding empty labels for alignment
        add(new JLabel(""));
        add(new JLabel(""));
        add(new JLabel(""));
        add(new JLabel(""));
    }

    private void changeUsername() { // changing username method changes from all the databases and updates the gui.
        String newUsername = usernameField.getText();
        if (!newUsername.equals(loggedInUser.getUsername())) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to change your username? This will update all your data in the system.", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String oldUsername = loggedInUser.getUsername();
                loggedInUser.setUsername(newUsername);
                try {
                    updateUsernameInFiles(oldUsername, newUsername);
                    loggedInUser.updateDB();
                    socialNetwork.updateUser(loggedInUser, oldUsername); // Update in SocialNetwork
                    JOptionPane.showMessageDialog(this, "Username changed successfully.");
                    updateAllReferences(oldUsername, newUsername); // New method to update references
                    logout();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error changing username: " + e.getMessage());
                }
            }
        }
    }

    private void changePassword() { // changing password method changes from all the databases and updates the gui.
        String currentPassword = JOptionPane.showInputDialog(this, "Enter current password:");
        if (!loggedInUser.getPassword().equals(currentPassword)) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect.");
            return;
        }
        String newPassword = new String(passwordField.getPassword());
        if (!newPassword.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to change your password?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                loggedInUser.setPassword(newPassword);
                try {
                    loggedInUser.updateDB();
                    socialNetwork.updateUser(loggedInUser, loggedInUser.getUsername()); // Update in SocialNetwork
                    JOptionPane.showMessageDialog(this, "Password changed successfully.");
                    logout();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error changing password: " + e.getMessage());
                }
            }
        }
    }

    private void changeCanBeSearchable() { // changing searchable info method changes from all the databases and updates the gui.
        boolean canBeSearchable = canBeSearchableCheckBox.isSelected();
        if (canBeSearchable != loggedInUser.isCanBeSearchable()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to change your searchability status?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                loggedInUser.setCanBeSearchable(canBeSearchable);
                try {
                    loggedInUser.updateDB();
                    socialNetwork.updateUser(loggedInUser, loggedInUser.getUsername()); // Update in SocialNetwork
                    JOptionPane.showMessageDialog(this, "Searchability status changed successfully.");
                    logout();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error changing searchability status: " + e.getMessage());
                }
            }
        }
    }

    private void updateAllReferences(String oldUsername, String newUsername) throws IOException {
        updateUsernameInGroups(oldUsername, newUsername);
        updateUsernameInFriends(oldUsername, newUsername);
    }


    private void updateUsernameInGroups(String oldUsername, String newUsername) throws IOException {
        for (Group group : SocialNetwork.getGroups().values()) {
            group.updateMemberUsername(oldUsername, newUsername);
        }
    }

    private void updateUsernameInFriends(String oldUsername, String newUsername) throws IOException {
        for (User user : SocialNetwork.getAllUsers()) {
            if (user.getFriends().containsKey(oldUsername)) {
                user.getFriends().remove(oldUsername);
                user.getFriends().put(newUsername, loggedInUser);
                user.updateDB();
            }
        }
    }

    private void logout() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose();
        new LoginScreen();
    }

    private void updateUsernameInFiles(String oldUsername, String newUsername) throws IOException {
        updateUsernameInFile("usersdb.txt", oldUsername, newUsername, false, true);
        updateUsernameInFile("postsdb.txt", oldUsername, newUsername, true, false);
        updateUsernameInFile("pmdb.txt", oldUsername, newUsername, true, false);
        updateUsernameInFile("grouppostsdb.txt", oldUsername, newUsername, true, false);
        updateUsernameInFile("groupsdb.txt", oldUsername, newUsername, true, false);
    }

    private void updateUsernameInFile(String fileName, String oldUsername, String newUsername, boolean replaceInContent, boolean isUsersDB) throws IOException {
        Path filePath = Paths.get(fileName);  // updates database with the new information that user gives to the gui.
        List<String> lines = Files.readAllLines(filePath);
        List<String> updatedLines = lines.stream()
                .map(line -> {
                    if (isUsersDB) {
                        String[] parts = line.split(":");
                        if (parts[0].equals(oldUsername)) {
                            parts[0] = newUsername;
                        }
                        if (parts.length > 3) {
                            parts[3] = parts[3].replace(oldUsername, newUsername);
                        }
                        return String.join(":", parts);
                    } else {
                        return replaceInContent ? line.replace(oldUsername, newUsername) : line;
                    }
                })
                .collect(Collectors.toList());

        Files.write(filePath, updatedLines);
    }
}