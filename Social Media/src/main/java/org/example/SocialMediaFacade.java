package org.example;

import java.io.IOException;

public class SocialMediaFacade {
    private UserFactory userFactory;

    public SocialMediaFacade() { // creating Abstract Factory in order to create new users from this factory.
        this.userFactory = AbstractFactoryProducer.getFactory();
    }

    public User createUser(String username, String password, boolean canBeSearchable) throws IOException {
        return userFactory.createUser(username, password, canBeSearchable);
    }  // creating User with the help of Abstract Factory.

    public void addPost(User user, String post) throws IOException { // adding post to users wallMessages
        user.addPost(post);
    }

    public void addFriend(User user, User newFriend) throws IOException { // adding new friend to their list.
        user.addFriend(newFriend);
    }
}