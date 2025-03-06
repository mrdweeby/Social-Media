package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class HomeScreen extends JFrame {
    private User loggedInUser;
    private JTextArea wallArea;
    private JTextField postField;
    private static final String CURRENT_PATH = ".";
    private static final String POSTSDATABASE_FILE_NAME = "postsdb.txt";

    public HomeScreen(User user) throws IOException {
        this.loggedInUser = user;
        setTitle("Home Screen");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JTabbedPane tabbedPane = new JTabbedPane();

        // wall tab (this panel opens after the login/register process)
        JPanel wallPanel = new JPanel(new BorderLayout());
        wallArea = new JTextArea();
        wallArea.setEditable(false);
        updateWall();
        wallPanel.add(new JScrollPane(wallArea), BorderLayout.CENTER);
        JPanel postPanel = new JPanel(new BorderLayout());
        postField = new JTextField();
        JButton postButton = new JButton("Post");
        postButton.addActionListener(new PostButtonListener());
        postPanel.add(postField, BorderLayout.CENTER);
        postPanel.add(postButton, BorderLayout.EAST);
        wallPanel.add(postPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Home", wallPanel);

        // friends tab
        JPanel friendsPanel = new SearchPanel(loggedInUser, this);
        tabbedPane.addTab("Search Friends", friendsPanel);

        // groups tab
        JPanel groupsPanel = new GroupPanel(loggedInUser);
        tabbedPane.addTab("Groups", groupsPanel);

        // messages tab
        JPanel messagesPanel = new MessagesPanel(loggedInUser);
        tabbedPane.addTab("Messages", messagesPanel);

        // profile tab
        JPanel profilePanel = new ProfilePanel(loggedInUser);
        tabbedPane.addTab("Profile", profilePanel);

        // settings tab
        JPanel settingsPanel = new SettingsPanel(loggedInUser);
        tabbedPane.addTab("Settings", settingsPanel);

        add(tabbedPane);
        setVisible(true);
    }

    public void updateWall() throws IOException {  //updates wall similar like updateDB() in User and Group classes.
        File dbFile = new File(CURRENT_PATH, POSTSDATABASE_FILE_NAME);
        BufferedReader reader = new BufferedReader(new FileReader(dbFile));
        StringBuilder wallContent = new StringBuilder();
        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
            String[] tokens = currentLine.split(":");
            if (tokens.length >= 2) {
                String username = tokens[0];
                String message = tokens[1];
                if (loggedInUser.getUsername().equals(username) || loggedInUser.getFriends().containsKey(username)) {
                    wallContent.insert(0, username + ": " + message + "\n");
                }
            }
        }
        reader.close();
        wallArea.setText(wallContent.toString());
    }

    private class PostButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String post = postField.getText();
            if (!post.isEmpty()) {
                try {
                    SocialMediaFacade smf = new SocialMediaFacade();
                    smf.addPost(loggedInUser,post);
                    loggedInUser.updateDB();
                    postField.setText("");
                    updateWall();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
