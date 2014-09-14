package pl.cierniewski.friendlygallery.dagger;

import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.squareup.okhttp.HttpResponseCache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import dagger.Module;
import dagger.Provides;
import pl.cierniewski.friendlygallery.BuildConfig;
import pl.cierniewski.friendlygallery.helper.LogHelper;

@Module(
        library = true,
        complete = false
)
public class BaseModule {
    private static final String TAG = "BaseModule";

    @Singleton
    @Provides
    @Named("picasso")
    public SSLSocketFactory providePicassoSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SSLSocket Factory Provider cannot be initialized.", e);
        } catch (KeyManagementException e) {
            throw new RuntimeException("SSLSocket Factory Provider cannot be initialized.", e);
        }
    }

    @Singleton
    @Provides
    @Named("picasso")
    public OkHttpClient providePicassoOkHttpClient(@ForApplication Context context,
                                            @Named("picasso") SSLSocketFactory sslSocketFactory) {

        HttpResponseCache responseCache = null;
        try {
            File httpCacheDir = new File(context.getCacheDir(), "cache");
            long httpCacheSize = 150 * 1024 * 1024; // 150 MiB
            responseCache = new HttpResponseCache(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i(TAG, "HTTP response cache installation failed:", e);
        }
        final OkHttpClient okHttpClient = new OkHttpClient();
        if (responseCache != null) {
            okHttpClient.setResponseCache(responseCache);
        }
        okHttpClient.setSslSocketFactory(sslSocketFactory);
        return okHttpClient;
    }

    @Provides
    @Singleton
    Picasso providePicasso(@ForApplication Context context,
                           @Named("picasso") OkHttpClient okHttpClient) {
        final LruCache lruCache = new LruCache(context);
        return new Picasso.Builder(context)
                .memoryCache(lruCache)
                .indicatorsEnabled(BuildConfig.DEBUG)
                .downloader(new OkHttpDownloader(okHttpClient))
                .build();
    }

    @Provides
    @Singleton
    public ContentResolver provideContentResolver(@ForApplication Context context) {
        return context.getContentResolver();
    }

    @Provides
    @Singleton
    public LocationManager provideLocationManager(@ForApplication Context context) {
        return (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    AccountManager provideAccountManager(@ForApplication Context context) {
        return AccountManager.get(context);
    }

    private static class SimpleThreadFactory implements ThreadFactory {
        private static int mThreadNum = 0;
        private static final Object mLock = new Object();

        private final String mThreadBaseName;
        private final int mThreadPriority;

        private SimpleThreadFactory(String threadBaseName, int threadPriority) {
            mThreadBaseName = threadBaseName;
            mThreadPriority = threadPriority;
        }

        @Override
        public Thread newThread(Runnable r) {
            final int threadNum;
            synchronized (mLock) {
                threadNum = mThreadNum++;
            }
            final Thread thread = new Thread(r);
            thread.setName(String.format(Locale.US, "%s-%d", mThreadBaseName, threadNum));
            thread.setPriority(mThreadPriority);
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    LogHelper.e(TAG, String.format("Uncaught exception in %s", thread.getName()), ex);
                }
            });
            return thread;
        }
    }

    @Provides
    @Singleton
    ListeningExecutorService provideExecutorService() {
        final SimpleThreadFactory threadFactory = new SimpleThreadFactory("Executor", Thread.MIN_PRIORITY);
        return MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5, threadFactory));
    }

    @Provides
    @Singleton
    NotificationManager provideNotificationManager(@ForApplication Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    @Singleton
    @ForApplication
    public Resources provideResource(@ForApplication Context context) {
        return context.getResources();
    }

    @Provides
    ConnectivityManager provideConnectivityManager(@ForApplication Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    PackageManager providePackageManager(@ForApplication Context context) {
        return context.getPackageManager();
    }

    @Provides
    ActivityManager provideActivityManager(@ForApplication Context context) {
        return (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

}
