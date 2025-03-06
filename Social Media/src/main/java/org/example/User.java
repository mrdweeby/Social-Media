package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Observer {
    private String username;
    private String password;
    private boolean canBeSearchable;
    private List<String> wallMessages;
    private Map<String, User> friends;
    private Map<String, Group> groups;
    private List<String> receivedMessages;
    private List<String> sentPrivateMessages;
    private int postCount;
    private static final String CURRENT_PATH = ".";
    private static final String USERDATABASE_FILE_NAME = "usersdb.txt";
    private static final String POSTSDATABASE_FILE_NAME = "postsdb.txt";
    private static final String PMDATABASE_FILE_NAME = "pmdb.txt";


    public User(String username, String password, boolean canBeSearchable) throws IOException {
        this.setUsername(username);
        this.setPassword(password);
        this.setCanBeSearchable(canBeSearchable);
        this.setFriends(new HashMap<>());
        this.setWallMessages(new ArrayList<>());
        this.setGroups(new HashMap<>());
        this.setReceivedMessages(new ArrayList<>());
        this.setSentPrivateMessages(new ArrayList<>());
        loadWallMessages();
        setPostCount(0);
    }

    public void addPost(String post) {
        wallMessages.add(post);
    }

    public void addFriend(User newFriend) throws IOException { // adding friends both current user's and newFriend's friendlist
        if (newFriend.isCanBeSearchable()) {
            friends.put(newFriend.getUsername(), newFriend);
            newFriend.getFriends().put(this.getUsername(), this);
            updateDB();
            newFriend.updateDB();
        }
    }

    public void addFriendByUsername(String username) {
        friends.put(username, null);
    }

    public void addGroupByName(String groupName) {
        groups.put(groupName, null);
    }

    public void sendPM(User other, String message) throws IOException { // sending private messages.
        BufferedWriter w3 = new BufferedWriter(new FileWriter(new File(CURRENT_PATH, PMDATABASE_FILE_NAME), true));
        StringBuilder sendInfo = new StringBuilder();
        sendInfo.append(this.getUsername()).append(":").append(other.getUsername()).append(":").append(message).append("\n");
        w3.write(sendInfo.toString());
        sentPrivateMessages.add(message);
        other.receivedMessages.add(message);
        w3.close();
    }

    @Override
    public void updateDB() throws IOException { // nearly the same in Group.java's updateDB() method.
        // Update userdb.txt
        File userFile = new File(CURRENT_PATH, USERDATABASE_FILE_NAME);
        BufferedReader rd1 = new BufferedReader(new FileReader(userFile));
        StringBuilder fileText = new StringBuilder();
        String curUser = this.getUsername();
        String currentLine;

        while ((currentLine = rd1.readLine()) != null) {
            String[] parts = currentLine.split(":");
            if (parts.length == 0 || !parts[0].equals(curUser)) {
                fileText.append(currentLine).append('\n');
            }
        }
        rd1.close();
        StringBuilder userInfo = new StringBuilder();
        userInfo.append(this.getUsername()).append(':').append(this.getPassword()).append(':').append(this.canBeSearchable).append(':');
        if (!friends.isEmpty()) {
            for (String friendUsername : friends.keySet()) {
                userInfo.append(friendUsername).append(",");
            }
            userInfo.setCharAt(userInfo.length() - 1, ':');
        } else {
            userInfo.append(':');
        }
        if (!groups.isEmpty()) {
            for (String groupName : groups.keySet()) {
                userInfo.append(groupName).append(",");
            }
            userInfo.setCharAt(userInfo.length() - 1, ':');
        } else {
            userInfo.append(':');
        }
        userInfo.append(wallMessages.size()).append('\n'); // updating post count
        fileText.append(userInfo);
        BufferedWriter w1 = new BufferedWriter(new FileWriter(userFile));
        w1.write(fileText.toString());
        w1.close();
        // update postsdb.txt
        File postsFile = new File(CURRENT_PATH, POSTSDATABASE_FILE_NAME);
        List<String> existingPosts = new ArrayList<>();
        BufferedReader r = new BufferedReader(new FileReader(postsFile));
        String line;

        while ((line = r.readLine()) != null) {
            existingPosts.add(line);
        }
        r.close();

        BufferedWriter w = new BufferedWriter(new FileWriter(postsFile, true));
        int existingPostCount = 0;
        for (String post : existingPosts) {
            String[] parts = post.split(":");
            if (parts.length > 0 && parts[0].equals(this.username)) {
                existingPostCount++;
            }
        }
        setPostCount(existingPostCount);
        // Add new posts to postsdb.txt
        if (existingPostCount < wallMessages.size()){
            w.write(this.username + ":" + wallMessages.get(wallMessages.size() - 1) + "\n");
        }
        w.close();
    }

    public void loadWallMessages() throws IOException { // loading posts from postsdb.txt
        BufferedReader r = new BufferedReader(new FileReader(POSTSDATABASE_FILE_NAME));
        String line;

        while ((line = r.readLine()) != null) {
            String[] parts = line.split(":");
            if (parts[0].equals(this.getUsername())) {
                wallMessages.add(parts[1]);
            }
        }
        r.close();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isCanBeSearchable() {
        return canBeSearchable;
    }

    public List<String> getSentPrivateMessages() {
        return sentPrivateMessages;
    }

    public List<String> getWallMessages() {
        return wallMessages;
    }

    public Map<String, User> getFriends() {
        return friends;
    }

    public Map<String, Group> getGroups() {
        return groups;
    }

    public List<String> getReceivedMessages() {
        return receivedMessages;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCanBeSearchable(boolean canBeSearchable) {
        this.canBeSearchable = canBeSearchable;
    }

    public void setWallMessages(List<String> wallMessages) {
        this.wallMessages = wallMessages;
    }

    public void setFriends(Map<String, User> friends) {
        this.friends = friends;
    }

    public void setGroups(Map<String, Group> groups) {
        this.groups = groups;
    }

    public void setReceivedMessages(List<String> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public void setSentPrivateMessages(List<String> sentPrivateMessages) {
        this.sentPrivateMessages = sentPrivateMessages;
    }

    public void resolveFriendsAndGroups() {
        // this method should be called after all users are loaded
        for (Map.Entry<String, User> entry : friends.entrySet()) {
            String friendUsername = entry.getKey();
            if (SocialNetwork.userExists(friendUsername)) {
                friends.put(friendUsername, SocialNetwork.getUser(friendUsername));
            }
        }
        for (Map.Entry<String, Group> entry : groups.entrySet()) {
            String groupName = entry.getKey();
            if (SocialNetwork.groupExists(groupName)) { // assuming there is a groupExists method in SocialNetwork
                groups.put(groupName, SocialNetwork.getGroup(groupName)); // assuming there is a getGroup method in SocialNetwork
            }
        }
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }
}