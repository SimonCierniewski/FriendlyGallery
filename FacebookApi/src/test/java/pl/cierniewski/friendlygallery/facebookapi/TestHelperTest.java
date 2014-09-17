package pl.cierniewski.friendlygallery.facebookapi;

import org.junit.Test;

import java.io.File;
import java.net.URL;

import pl.cierniewski.friendlygallery.facebookapi.base.TestHelper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.describedAs;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class TestHelperTest {

    @Test
    public void testRandom() throws Exception {
        final String s1 = TestHelper.randomString();
        final String s2 = TestHelper.randomString();
        assertThat(s1, is(not(equalTo(s2))));
    }


    @Test
    public void testRobotImage() throws Exception {
        final File robot = TestHelper.getRobotFile();
        assertThat(robot, is(notNullValue()));
        assertThat(robot.exists(), describedAs("exists", equalTo(true)));
    }

    @Test
    public void testRobotImageUri() throws Exception {
        final URL robot = TestHelper.getRobotFileUri();
        assertThat(robot, is(notNullValue()));
    }
}
