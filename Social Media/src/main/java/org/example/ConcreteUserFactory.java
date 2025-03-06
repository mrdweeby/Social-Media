package org.example;

import java.io.IOException;

public class ConcreteUserFactory implements UserFactory { //it provides the actual logic for creating User objects.
    @Override
    public User createUser(String username, String password, boolean canBeSearchable) throws IOException {
        return new User(username, password, canBeSearchable);
    }
}