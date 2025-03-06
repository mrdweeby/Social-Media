package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginScreen extends JFrame{
    private JTextField usernameField;
    private JPasswordField passwordField;
    public LoginScreen(){
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3,2));
        JLabel usernameLabel = new JLabel("Username");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginButtonListener());

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new RegisterButtonListener());

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);
        add(panel);
        setVisible(true);
    }

    private class LoginButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String (passwordField.getPassword());

            if (SocialNetwork.userExists(username)){
                User user = SocialNetwork.getUser(username);
                if (user.getPassword().equals(password)){
                    JOptionPane.showMessageDialog(LoginScreen.this, "Welcome " + username);
                    try {
                        new HomeScreen(user);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    dispose();
                }else{
                    JOptionPane.showMessageDialog(LoginScreen.this, "Incorrect password!");
                }
            }else{
                JOptionPane.showMessageDialog(LoginScreen.this, "User does not exist!");
            }

        }
    }

    private class RegisterButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            new RegisterScreen();
            dispose();
        }
    }
}
