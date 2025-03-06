# Social Media Application

## 📌 Overview
This project is a **Social Media Application** developed as part of the **Advanced Object-Oriented Programming (AOOP)** course at **Ege University, Faculty of Engineering, Computer Engineering Department**. The system enables users to **connect, communicate, and share content**, similar to platforms like **Facebook** and **Twitter**.

## 🚀 Features
### 🔹 User Authentication
- **User Registration & Login** with username and password.
- **Searchable Profiles**: Users can choose to be discoverable by others.

### 🔹 Social Networking
- **Friend System**: Users can **add and manage friends**.
- **Private Messaging**: Secure one-on-one messaging feature.
- **Groups**: Users can **create and join groups** to engage with communities.

### 🔹 Content Sharing
- **Posting**: Users can share **text-based posts**.
- **Group Discussions**: Users can post within groups.
- **News Feed**: View posts from **friends and groups**.

### 🔹 Profile & Settings
- **User Profile Management**: View posts, friends, and group memberships.
- **Settings**: Change username, password, and privacy settings.

## 🎨 Software Design Patterns Used
- **Observer Pattern**: Updates the database when user actions occur.
- **Singleton Pattern**: Ensures a single instance of the social network manager.
- **Abstract Factory Pattern**: Efficiently creates user objects.
- **Facade Pattern**: Simplifies complex operations like user creation and message handling.

## 🛠️ Technologies Used
- **Java (Swing for GUI)**
- **File-Based Storage** (e.g., `usersdb.txt`, `postsdb.txt`, `pmdb.txt`)
- **JUnit** for **Unit Testing**

## 📜 Database Structure
The system uses **file-based storage** instead of a traditional database. The main files include:
- `usersdb.txt` → Stores **user credentials and friendships**.
- `postsdb.txt` → Stores **user posts**.
- `pmdb.txt` → Stores **private messages**.
- `groupsdb.txt` → Stores **group memberships**.
- `grouppostsdb.txt` → Stores **group posts**.

## 🏗️ Project Structure
```
├── src/
│   ├── User.java              # User class with attributes and methods
│   ├── Group.java             # Group class for managing groups
│   ├── SocialNetwork.java     # Singleton class managing users and groups
│   ├── LoginScreen.java       # GUI for user login
│   ├── RegisterScreen.java    # GUI for user registration
│   ├── HomeScreen.java        # Main interface with tabs
│   ├── SearchPanel.java       # Friend search functionality
│   ├── GroupPanel.java        # Group management
│   ├── MessagesPanel.java     # Private messaging system
│   ├── ProfilePanel.java      # User profile view
│   ├── SettingsPanel.java     # User settings and account management
│   ├── FriendsSelectionDialog.java # Dialog for adding friends to groups
│
├── tests/
│   ├── SocialMediaTest.java   # JUnit test cases
│
├── README.md
```

## 💻 Installation & Usage
1. **Clone the repository**
   ```bash
   git clone https://github.com/mrdweeby/Social-Media.git
   ```
2. **Compile & Run**
   ```bash
   javac src/*.java
   java src.LoginScreen
   ```
3. **Login or Register**
   - New users must **register** before logging in.
   - Existing users can log in using their **credentials**.

## 🔬 Unit Testing
JUnit tests validate core functionalities:
- **User creation and authentication**
- **Friendship management**
- **Posting system**
- **Private messaging**
```java
@Test
public void testUserCreation() throws IOException {
    User user = new User("testUser", "password", true);
    assertEquals("testUser", user.getUsername());
}
```

## 👨‍💻 Contributors
- **Bülent Yıldırım**  
- **Emir Kahraman**  
- **Alp Kutay Köksal**  

📅 **Project Submission Date:** *23/05/2024*

