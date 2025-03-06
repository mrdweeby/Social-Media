package org.example;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SocialNetwork {
    private static SocialNetwork instance;
    private static Map<String, User> users = new HashMap<>();
    private static Map<String, Group> groups = new HashMap<>();
    private static final String CURRENT_PATH = ".";
    private static final String USERDATABASE_FILE_NAME = "usersdb.txt";
    private static final String GROUPDATABASE_FILE_NAME = "groupsdb.txt";

    static {
        try {
            loadUsers();
            loadGroups();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadUsers() throws IOException {
        File usersFile = new File(CURRENT_PATH, USERDATABASE_FILE_NAME);
        BufferedReader reader = new BufferedReader(new FileReader(usersFile));
        String line;

        // create all users without setting friends and groups
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(":");
            if (tokens.length >= 6) {
                String username = tokens[0];
                String password = tokens[1];
                boolean isSearchable = Boolean.parseBoolean(tokens[2]);
                String[] friendUsernames = tokens[3].split(",");
                String[] groupNames = tokens[4].split(",");
                int postCount = Integer.parseInt(tokens[5]);
                SocialMediaFacade smf = new SocialMediaFacade();
                User user = smf.createUser(username,password,isSearchable);
                user.setPostCount(postCount);
                for (String friendUsername : tokens[3].split(",")) {
                    user.addFriendByUsername(friendUsername);
                }
                for (String groupName : groupNames) {
                    if (!groupName.isEmpty()) {
                        user.addGroupByName(groupName);
                    }
                }
                users.put(username, user);
            }
        }
        reader.close();
        // resolve friends and groups
        for (User user : users.values()) {
            user.resolveFriendsAndGroups();
        }
    }

    public static void loadGroups() throws IOException {
        File groupsFile = new File(CURRENT_PATH, GROUPDATABASE_FILE_NAME);
        BufferedReader reader = new BufferedReader(new FileReader(groupsFile));
        String line;

        // create all groups without setting members
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(":");
            String groupName = tokens[0];
            Group group = new Group(groupName);

            if (tokens.length > 1 && !tokens[1].isEmpty()) {
                for (String memberUsername : tokens[1].split(",")) {
                    group.getMembers().put(memberUsername, null); // Placeholder
                }
            }

            groups.put(groupName, group);
        }
        reader.close();

        // resolve members
        for (Group group : groups.values()) {
            group.resolveMembers();
        }
    }

    public static void updateUsers() throws IOException { // new user profile is created
        File usersFile = new File(CURRENT_PATH, USERDATABASE_FILE_NAME);
        BufferedReader reader = new BufferedReader(new FileReader(usersFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(":");
            String username = tokens[0];
            if (!users.containsKey(username)) {
                String password = tokens[1];
                boolean isSearchable = Boolean.parseBoolean(tokens[2]);
                User user = new User(username, password, isSearchable);
                users.put(username, user);
            }
        }
        reader.close();

        //resolve friends and groups for new users
        for (User user : users.values()) {
            user.resolveFriendsAndGroups();
        }
    }

    public static SocialNetwork getInstance() { // in order to use the instance of this class.
        if (instance == null) {
            instance = new SocialNetwork();
        }
        return instance;
    }

    public void updateUser(User user, String oldUsername) {
        if (!user.getUsername().equals(oldUsername)) {
            users.remove(oldUsername);
        }
        users.put(user.getUsername(), user);
    }

    public static boolean userExists(String username) {
        return users.containsKey(username);
    }

    public static User getUser(String username) {
        return users.get(username);
    }

    public static boolean groupExists(String groupName) {
        return groups.containsKey(groupName);
    }

    public static Group getGroup(String groupName) {
        return groups.get(groupName);
    }

    public static Map<String, Group> getGroups() {
        return groups;
    }

    public static Collection<User> getAllUsers(){
        return users.values();
    }
}