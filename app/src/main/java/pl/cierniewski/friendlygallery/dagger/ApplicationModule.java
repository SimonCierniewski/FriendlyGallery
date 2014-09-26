package pl.cierniewski.friendlygallery.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.cierniewski.friendlygallery.MainApplication;
import pl.cierniewski.friendlygallery.facebookapi.ProductionServer;
import pl.cierniewski.friendlygallery.facebookapi.Server;

@Module (
        includes = {BaseModule.class},
        injects = MainApplication.class,
        library = true
)
public class ApplicationModule {

    private final MainApplication mMainApplication;

    public ApplicationModule(MainApplication mainApplication) {
        mMainApplication = mainApplication;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return mMainApplication;
    }

    @Singleton
    @Provides
    Server provideServer() {
        return new ProductionServer();
    }
}