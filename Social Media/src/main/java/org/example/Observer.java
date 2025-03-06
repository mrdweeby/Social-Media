package org.example;

import java.io.IOException;

public interface Observer { //we need this interface in order to update our databases.
    void updateDB() throws IOException;
}
