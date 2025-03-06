package org.example;

import java.io.IOException;

public interface UserFactory {
    User createUser(String username, String password, boolean canBeSearchable) throws IOException;
}