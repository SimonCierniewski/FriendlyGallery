package pl.cierniewski.friendlygallery.facebookapi.base;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class TestHelper {

    public static String randomString() {
        return "test_" + Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    public static File getRobotFile() {
        try {
            return new File(TestHelper.class.getResource("robot.png").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not open robot.png", e);
        }
    }

    public static URL getRobotFileUri() {
        return TestHelper.class.getResource("robot.png");
    }
}
