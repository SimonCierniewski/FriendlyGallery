package pl.cierniewski.friendlygallery;

import android.app.Application;

import com.facebook.LoggingBehavior;
import com.facebook.Settings;
import com.squareup.okhttp.internal.http.HttpTransport;

import java.util.logging.Level;
import java.util.logging.Logger;

import dagger.ObjectGraph;
import pl.cierniewski.friendlygallery.dagger.ApplicationModule;
import pl.cierniewski.friendlygallery.helper.LogHelper;

public class MainApplication extends Application {

    private static final String TAG = "MainApplication";
    private ObjectGraph mApplicationGraph;

    private Object mApplicationModule = new ApplicationModule(this);

    @Override
    public void onCreate() {
        super.onCreate();

        initializeDagger();

        if (BuildConfig.DEBUG) {
            Logger.getLogger(HttpTransport.class.getName()).setLevel(Level.CONFIG);
            LogHelper.getFacebookDataCollectorLogger().setLevel(Level.FINEST);
        } else {
//            Crashlytics.start(this);
//            mLogHelper.registerCrashlyticsKeys();
        }

        // Facebook logging
        Settings.addLoggingBehavior(LoggingBehavior.REQUESTS);
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);
        Settings.addLoggingBehavior(LoggingBehavior.CACHE);
        Settings.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
        Settings.addLoggingBehavior(LoggingBehavior.DEVELOPER_ERRORS);
        Settings.setIsLoggingEnabled(true);
    }

    public void setApplicationModule(Object applicationModule) {
        mApplicationModule = applicationModule;
    }

    private void initializeDagger() {
        mApplicationGraph = ObjectGraph.create(mApplicationModule);
        mApplicationGraph.inject(this);
    }

    public ObjectGraph getApplicationGraph() {
        return mApplicationGraph;
    }

    public static MainApplication fromApplication(Application application) {
        return (MainApplication) application;
    }
}
