package pl.cierniewski.friendlygallery.facebookapi;

import org.junit.Test;

import pl.cierniewski.friendlygallery.facebookapi.parser.AndroidUnderscoreNamingStrategy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class NamingPolicyTest {

    @SuppressWarnings("UnusedDeclaration")
    public static class TestClass {
        public String someName;
        public String some;
        public String mSomeName;
        public String mMyName;
        public String myName;
        public String m;
        public String y;
        public String MyName;
    }

    @Test
    public void testNamingPolicy() throws Exception {
        final AndroidUnderscoreNamingStrategy test = new AndroidUnderscoreNamingStrategy();
        final String someName = test.translateName(TestClass.class.getField("someName"));
        assertThat(someName, is(equalTo("some_name")));

        final String some = test.translateName(TestClass.class.getField("some"));
        assertThat(some, is(equalTo("some")));

        final String mSomeName = test.translateName(TestClass.class.getField("mSomeName"));
        assertThat(mSomeName, is(equalTo("some_name")));

        final String mMyName = test.translateName(TestClass.class.getField("mMyName"));
        assertThat(mMyName, is(equalTo("my_name")));

        final String myName = test.translateName(TestClass.class.getField("myName"));
        assertThat(myName, is(equalTo("my_name")));

        final String m = test.translateName(TestClass.class.getField("m"));
        assertThat(m, is(equalTo("m")));

        final String y = test.translateName(TestClass.class.getField("y"));
        assertThat(y, is(equalTo("y")));

        final String MyName = test.translateName(TestClass.class.getField("MyName"));
        assertThat(MyName, is(equalTo("my_name")));

    }
}
