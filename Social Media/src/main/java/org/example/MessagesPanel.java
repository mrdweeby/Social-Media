package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MessagesPanel extends JPanel {
    private User loggedInUser;
    private JList<String> friendsList;
    private JTextArea chatArea;
    private DefaultListModel<String> friendsListModel;
    private Map<String, StringBuilder> chatHistory;
    private MessagesPanel instance;

    public MessagesPanel(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.chatHistory = new HashMap<>();

        setLayout(new BorderLayout());

        // friends list panel
        JPanel friendsListPanel = new JPanel(new BorderLayout());
        friendsListModel = new DefaultListModel<>();
        friendsList = new JList<>(friendsListModel);
        friendsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendsList.addListSelectionListener(e -> loadChatHistory(friendsList.getSelectedValue()));
        JScrollPane friendsListScrollPane = new JScrollPane(friendsList);
        friendsListPanel.add(new JLabel("Friends"), BorderLayout.NORTH);
        friendsListPanel.add(friendsListScrollPane, BorderLayout.CENTER);
        add(friendsListPanel, BorderLayout.WEST);

        // chat area panel
        JPanel chatAreaPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatAreaScrollPane = new JScrollPane(chatArea);
        chatAreaPanel.add(new JLabel("Chat"), BorderLayout.NORTH);
        chatAreaPanel.add(chatAreaScrollPane, BorderLayout.CENTER);

        JTextField newMessageField = new JTextField();
        JButton sendMessageButton = new JButton("Send");
        sendMessageButton.addActionListener(e -> {
            try {
                sendMessage(newMessageField.getText(), friendsList.getSelectedValue());
                newMessageField.setText("");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        JPanel newMessagePanel = new JPanel(new BorderLayout());
        newMessagePanel.add(newMessageField, BorderLayout.CENTER);
        newMessagePanel.add(sendMessageButton, BorderLayout.EAST);
        chatAreaPanel.add(newMessagePanel, BorderLayout.SOUTH);
        add(chatAreaPanel, BorderLayout.CENTER);

        loadFriends();
        loadChatHistoryFromFile();
    }

    private void loadFriends() {
        friendsListModel.clear();
        for (User friend : loggedInUser.getFriends().values()) {
            if (friend != null){
                friendsListModel.addElement(friend.getUsername());
            }
        }
    }

    private void loadChatHistory(String friendUsername) {
        chatArea.setText("");
        if (friendUsername != null && chatHistory.containsKey(friendUsername)) {
            chatArea.append(chatHistory.get(friendUsername).toString());
        }
    }

    private void loadChatHistoryFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("pmdb.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String sender = parts[0];
                    String receiver = parts[1];
                    String message = parts[2];

                    // check if the message is sent by or received by the logged in user
                    if (sender.equals(loggedInUser.getUsername()) || receiver.equals(loggedInUser.getUsername())) {
                        String key = (sender.equals(loggedInUser.getUsername()) ? receiver : sender);

                        chatHistory.putIfAbsent(key, new StringBuilder());
                        chatHistory.get(key).append(sender).append(": ").append(message).append("\n");
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading chat history: " + e.getMessage());
        }
    }

    private void sendMessage(String message, String friendUsername) throws IOException {
        if (friendUsername == null || friendUsername.isEmpty() || message.isEmpty()) return;

        String sender = loggedInUser.getUsername();
        String receiver = friendUsername;

        // append message to chat history
        chatHistory.putIfAbsent(friendUsername, new StringBuilder());
        chatHistory.get(friendUsername).append(sender).append(": ").append(message).append("\n");

        // refresh chat area
        StringBuilder sb = new StringBuilder();
        sb.append(sender).append(": ").append(message).append("\n");
        chatArea.append(sb.toString());

        // save message to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("pmdb.txt", true))) {
            bw.write(sender + ":" + receiver + ":" + message);
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving message: " + e.getMessage());
        }
    }

    public void updateFriendsList() {
        friendsListModel.clear();
        for (String friendUsername : loggedInUser.getFriends().keySet()) {
            friendsListModel.addElement(friendUsername);
        }
    }
}