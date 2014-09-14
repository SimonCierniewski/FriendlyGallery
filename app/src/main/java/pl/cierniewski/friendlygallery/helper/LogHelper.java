package pl.cierniewski.friendlygallery.helper;

import android.util.Log;

import pl.cierniewski.friendlygallery.BuildConfig;

public class LogHelper {

    public static void e(String tag, String message, Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, e);
        } else {
//            Crashlytics.log(Log.ERROR, tag, message);
//            Crashlytics.logException(e);
        }
    }

    public static void e(String tag, Throwable e) {
        final String message = e.getMessage();
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, e);
        } else {
//            Crashlytics.log(Log.ERROR, tag, message);
//            Crashlytics.logException(e);
        }
    }
}
