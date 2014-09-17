package pl.cierniewski.friendlygallery.facebookapi;

import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.base.Objects;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class GistrHttpRequestInitializer implements HttpRequestInitializer {

    static class UserAgentInterceptor implements HttpExecuteInterceptor {

        @Inject
        UserAgentInterceptor(UserAgentFactory userAgentFactory) {
            mUserAgentFactory = userAgentFactory;
        }

        private UserAgentFactory mUserAgentFactory;

        @Override
        public void intercept(HttpRequest request) throws IOException {
            final HttpHeaders headers = request.getHeaders();
            headers.setUserAgent(mUserAgentFactory.getUserAgent());
        }
    }



    static class AuthTokenInterceptor implements HttpExecuteInterceptor {

        @Inject
        AuthTokenInterceptor(AuthTokenFactory authTokenFactory) {
            mAuthTokenFactory = authTokenFactory;
        }
        @Inject
        Server
        mServer;

        private AuthTokenFactory mAuthTokenFactory;

        @Override
        public void intercept(HttpRequest request) throws IOException {
            final HttpHeaders headers = request.getHeaders();
            headers.put("X-API-VERSION", checkNotNull(mServer.getVersion()));

            final String authToken = mAuthTokenFactory.getAuthToken();
            if (!Objects.equal(authToken, AuthTokenFactory.NOT_LOGGED_IN)) {
                headers.put("X-AUTH-TOKEN", checkNotNull(authToken));
            }
        }
    }


    static class LanguageInterceptor implements HttpExecuteInterceptor {

        @Inject
        LanguageInterceptor() {
        }

        @Override
        public void intercept(HttpRequest request) throws IOException {
            final HttpHeaders headers = request.getHeaders();

            final String language = getLanguageHeader();
            if (language != null) {
                headers.put("Accept-Language", language);
            }

        }

        public static String getLanguageHeader() {
            final Locale locale = Locale.getDefault();
            if (locale == null) {
                return null;
            }
            final String language = locale.getLanguage();
            if (Strings.isNullOrEmpty(language)) {
                return null;
            }
            final String country = locale.getCountry();
            if (Strings.isNullOrEmpty(country)) {
                return language;
            } else {
                return String.format(Locale.US, "%s-%s, %s q=0.7", language, country, language);
            }
        }
    }

    @Inject
    HttpUnsuccessfulResponseHandler errorHandler;

    @Inject
    UserAgentInterceptor mUserAgentInterceptor;
//    @Inject
//    AuthTokenInterceptor mAuthTokenInterceptor;
    @Inject
    LanguageInterceptor mLanguageInterceptor;

    @Inject
    public GistrHttpRequestInitializer() {
    }

    @Override
    public void initialize(HttpRequest request) throws IOException {
        request.setInterceptor(new HttpExecuteInterceptor() {
            @Override
            public void intercept(HttpRequest request) throws IOException {
                mUserAgentInterceptor.intercept(request);
//                mAuthTokenInterceptor.intercept(request);
                mLanguageInterceptor.intercept(request);
            }
        });
        request.setUnsuccessfulResponseHandler(errorHandler);

    }
}
