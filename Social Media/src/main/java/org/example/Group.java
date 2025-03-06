package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group implements Observer{
    private String groupName;
    private List<String> posts;
    private Map<String, User> members;
    private static final String CURRENT_PATH = ".";
    private static final String GROUPDATABASE_FILE_NAME = "groupsdb.txt";
    private static final String GROUPPOSTDATABASE_FILE_NAME = "grouppostsdb.txt";
    private int groupPostCount;

    public Group(String groupName) throws IOException {
        this.setGroupName(groupName);
        this.setMembers(new HashMap<>());
        this.setPosts(new ArrayList<>());
        loadGroupPosts();
        setGroupPostCount(0);
    }

    public void addMember(User newMember) throws IOException {
        members.put(newMember.getUsername(), newMember);
        newMember.getGroups().put(this.getGroupName(), this); // We also put this group to new member's groupsList
        updateDB();
    }

    public void addPost(User loggedInUser, String message){
        StringBuilder sb = new StringBuilder();
        sb.append(loggedInUser.getUsername()).append(":").append(message).append("\n");
        posts.add(sb.toString());
    }

    @Override
    public void updateDB() throws IOException {
        File groupFile = new File(CURRENT_PATH, GROUPDATABASE_FILE_NAME);
        BufferedReader rd1 = new BufferedReader(new FileReader(groupFile));
        StringBuilder fileText = new StringBuilder();
        String curGroup = this.getGroupName();
        String currentLine;
        boolean groupFound = false;

        while ((currentLine = rd1.readLine()) != null) { // reading line by line in the groupsdb.txt
            String[] parts = currentLine.split(":");
            if (parts[0].equals(curGroup)) {
                StringBuilder groupInfo = new StringBuilder();
                for (String memberUsername : members.keySet()) { // we split members in the group with comma.
                    groupInfo.append(memberUsername).append(",");
                }
                if (groupInfo.length() > 0) {
                    groupInfo.setCharAt(groupInfo.length() - 1, '\n'); // we added this information in the last row.
                }
                fileText.append(curGroup).append(":").append(groupInfo);
                groupFound = true;
            } else {
                fileText.append(currentLine).append(System.lineSeparator());
            }
        }
        rd1.close();

        if (!groupFound) { // if not in database, we added informations into the database
            StringBuilder groupInfo = new StringBuilder();
            for (String memberUsername : members.keySet()) {
                groupInfo.append(memberUsername).append(",");
            }
            if (groupInfo.length() > 0) {
                groupInfo.setCharAt(groupInfo.length() - 1, '\n'); // Replace the last comma with a newline
            }
            fileText.append(curGroup).append(":").append(groupInfo);
        }

        BufferedWriter w1 = new BufferedWriter(new FileWriter(groupFile));
        w1.write(fileText.toString());
        w1.close();

        // updating grouppostsdb.txt
        File groupPostsFile = new File(CURRENT_PATH, GROUPPOSTDATABASE_FILE_NAME);
        List<String> existingPosts = new ArrayList<>();
        BufferedReader rd2 = new BufferedReader(new FileReader(groupPostsFile));
        String line;
        while ((line = rd2.readLine()) != null) { // reader reads all the lines in the grouppostsdb.txt
            existingPosts.add(line);
        }
        rd2.close();

        BufferedWriter w3 = new BufferedWriter(new FileWriter(groupPostsFile, true) );
        int existingPostCount = 0;
        for (String existingPost : existingPosts) { // if reader saw the groups post(s), it will increment the existingPostCount by one.
            String[] parts = existingPost.split(":");
            if (parts.length > 0 && parts[0].equals(curGroup)) {
                existingPostCount++;
            }
        }
        setGroupPostCount(existingPostCount);
        if (existingPostCount < posts.size()) {
            w3.write(this.groupName + ":" + posts.get(posts.size() - 1)); // adding new post to the last row
        }
        w3.close();
    }

    public void loadGroupPosts() throws IOException { // loading from grouppostsdb.txt
        BufferedReader r = new BufferedReader(new FileReader(GROUPPOSTDATABASE_FILE_NAME));
        StringBuilder fileText = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            String[] parts = line.split(":");
            if (parts[0].equals(this.getGroupName())) {
                fileText.append(parts[1]).append(":").append(parts[2]).append("\n");
                posts.add(fileText.toString());
            }
        }
        r.close();
    }

    public void updateMemberUsername(String oldUsername, String newUsername) throws IOException { // settings.
        if (members.containsKey(oldUsername)) {
            members.remove(oldUsername);
            members.put(newUsername, SocialNetwork.getUser(newUsername));
            updateDB();
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public List<String> getPosts() {
        return posts;
    }

    public Map<String, User> getMembers() {
        return members;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setPosts(List<String> posts) {
        this.posts = posts;
    }

    public void setMembers(Map<String, User> members) {
        this.members = members;
    }

    public void resolveMembers() {
        for (Map.Entry<String, User> entry : members.entrySet()) {
            String memberUsername = entry.getKey();
            if (SocialNetwork.userExists(memberUsername)) {
                members.put(memberUsername, SocialNetwork.getUser(memberUsername));
            }
        }
    }

    public int getMemberCount() {
        return members.size();
    }

    public int getGroupPostCount() {
        return groupPostCount;
    }

    public void setGroupPostCount(int groupPostCount) {
        this.groupPostCount = groupPostCount;
    }
}