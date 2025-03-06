package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox searchableCheckBox;
    public RegisterScreen() {
        setTitle("Register");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel searchableLabel = new JLabel("Searchable:");
        searchableCheckBox = new JCheckBox();

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new RegisterButtonListener());

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(searchableLabel);
        panel.add(searchableCheckBox);
        panel.add(registerButton);

        add(panel);
        setVisible(true);
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            boolean searchable = searchableCheckBox.isSelected();

            try{
                if (!SocialNetwork.userExists(username)){ // if there is no user that has the same username
                    SocialMediaFacade smf = new SocialMediaFacade(); // we call SocialMediaFacade
                    User user = smf.createUser(username,password,searchable); // in order to create our user with using Abstract Factory.
                    user.updateDB();
                    SocialNetwork.updateUsers();
                    JOptionPane.showMessageDialog(RegisterScreen.this, "User " + username + " has been registered successfully.");
                    new LoginScreen();
                    dispose();
                }else{
                    JOptionPane.showMessageDialog(RegisterScreen.this, "User " + username + " already exists.");
                }
            }catch(Exception ex){
                JOptionPane.showMessageDialog(RegisterScreen.this, ex.getMessage());
            }
        }
    }
}
