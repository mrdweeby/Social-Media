import org.example.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;



public class SocialMediaTest {

    @Test
    public void testUserCreation() throws IOException{
        SocialMediaFacade smf = new SocialMediaFacade();
        User user = smf.createUser("test","test",true);
        assertNotNull(user);
        assertEquals("test", user.getUsername());
    }
    @Test
    public void testAddFriend() throws IOException{
        SocialMediaFacade smf = new SocialMediaFacade();
        User user = smf.createUser("test","test",true);
        User newUser = smf.createUser("test2","test2",true);
        if (user != null && newUser != null) {
            newUser.addFriend(user);
            assertTrue(newUser.getFriends().containsKey(user.getUsername()));
        }
    }
    @Test
    public void testAddPost() throws IOException{
        SocialMediaFacade smf = new SocialMediaFacade();
        User user = smf.createUser("test","test",true);
        user.addPost("asdf");
        assertTrue(user.getWallMessages().contains("asdf"));
    }
    @Test
    public void testPM() throws IOException{
        SocialMediaFacade smf = new SocialMediaFacade();
        User user = smf.createUser("test","test",true);
        User asdf = smf.createUser("test2","test2",true);
        user.sendPM(asdf,"asdfgh");
        assertTrue(asdf.getReceivedMessages().contains("asdfgh"));
    }
}
