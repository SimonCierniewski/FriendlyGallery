package pl.cierniewski.friendlygallery;

import android.app.Application;

import com.squareup.okhttp.internal.http.HttpTransport;

import java.util.logging.Level;
import java.util.logging.Logger;

import dagger.ObjectGraph;
import pl.cierniewski.friendlygallery.dagger.ApplicationModule;

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
        } else {
//            Crashlytics.start(this);
//            mLogHelper.registerCrashlyticsKeys();
        }
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
