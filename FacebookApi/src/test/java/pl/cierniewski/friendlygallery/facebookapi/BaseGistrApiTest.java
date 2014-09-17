package pl.cierniewski.friendlygallery.facebookapi;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.okhttp.OkHttpTransport;

import org.junit.After;
import org.junit.Before;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import pl.cierniewski.friendlygallery.facebookapi.base.UserData;

public abstract class BaseGistrApiTest {

    @Module(
            injects = {
                    FacebookApi.class,
                    GistrUsersApiTest.class},
            includes = MockModule.class,
            overrides = true
    )
    static class LoggedInMockModule {

        @Singleton
        @Provides
        AuthTokenFactory provideAuthTokenFactory() {
            return new AuthTokenFactory() {
                @Override
                public String getAuthToken() {
                    return UserData.AUTH_TOKEN;
                }
            };
        }

    }

    @Module(
            injects = {FacebookApi.class},
            includes = ApiModule.class
    )
    static class MockModule {

        @Provides
        @Singleton
        HttpTransport provideHttpTransport() {
            return new OkHttpTransport.Builder()
                    .build();
        }

        @Singleton
        @Provides
        UserAgentFactory provideUserAgentFactory() {
            return new UserAgentFactory() {
                @Override
                public String getUserAgent() {
                    return "Testing";
                }

            };
        }

        @Singleton
        @Provides
        AuthTokenFactory provideAuthTokenFactory() {
            return new AuthTokenFactory() {
                @Override
                public String getAuthToken() {
                    return NOT_LOGGED_IN;
                }
            };
        }

        @Singleton
        @Provides
        Server provideServer() {
            return new ProductionServer();
        }

    }

    private static Handler sConsoleHandler = new Handler() {

        @Override
        public void close() throws SecurityException {
        }

        @Override
        public void flush() {
        }

        @Override
        public void publish(LogRecord record) {
            // default ConsoleHandler will print >= INFO to System.err
            if (record.getLevel().intValue() < Level.INFO.intValue()) {
                System.out.println(record.getMessage());
            }
        }
    };

    @Before
    public void setUpInjector() throws Exception {
        ObjectGraph.create(new LoggedInMockModule()).inject(this);
    }

    @Before
    public void setUpLogger() throws Exception {
        Logger logger = Logger.getLogger(HttpTransport.class.getName());
        logger.setLevel(Level.CONFIG);
        logger.addHandler(sConsoleHandler);
    }

    @After
    public void tearDownLogger() throws Exception {
        Logger logger = Logger.getLogger(HttpTransport.class.getName());
        logger.removeHandler(sConsoleHandler);
    }

}
