package pl.cierniewski.friendlygallery.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.cierniewski.friendlygallery.MainApplication;

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
}