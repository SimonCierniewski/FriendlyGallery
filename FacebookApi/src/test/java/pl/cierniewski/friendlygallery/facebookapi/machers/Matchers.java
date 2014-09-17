package pl.cierniewski.friendlygallery.facebookapi.machers;

import com.google.api.client.http.GenericUrl;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class Matchers {

    public static Matcher<String> urlWithParameter(final String field, final Matcher<String> parameterMatcher) {
        return new TypeSafeMatcher<String>() {
            @Override
            protected boolean matchesSafely(String url) {
                if (url == null || "".equals(url)) return false;

                final GenericUrl genericUrl = new GenericUrl(url);
                final Object parameter = genericUrl.getFirst(field);
                return parameterMatcher.matches(parameter);
            }

            @Override
            public void describeTo(Description description) {
                description
                        .appendText("query parameter: ")
                        .appendValue(field)
                        .appendText(" matches ")
                        .appendDescriptionOf(parameterMatcher);
            }
        };
    }

}
