package pl.cierniewski.friendlygallery.dagger;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.cierniewski.friendlygallery.AppConst;
import pl.cierniewski.friendlygallery.MainActivity;


@Module(
        injects = {
                MainActivity.class
        },
        addsTo = ApplicationModule.class,
        library = true
)
public class ActivityModule {

    private final FragmentActivity mActivity;

    public ActivityModule(FragmentActivity activity) {
        mActivity = activity;
    }

    @Provides
    @Singleton
    @ForActivity
    public Context activityContext() {
        return mActivity;
    }

    @Provides
    @Singleton
    public AssetManager provideAssetManager(@ForActivity Context context) {
        return context.getAssets();
    }

    @Provides
    @Singleton
    public Resources provideResource(@ForActivity Context context) {
        return context.getResources();
    }

    @Provides
    @Singleton
    @RobotoLight
    public Typeface provideTypefaceRobotoLight(@ForActivity Context context) {
        return Typeface.createFromAsset(context.getAssets(), AppConst.ROBOTO_LIGHT_FONT);
    }

    @Provides
    @Singleton
    @RobotoMedium
    public Typeface provideTypefaceRobotoMedium(@ForActivity Context context) {
        return Typeface.createFromAsset(context.getAssets(), AppConst.ROBOTO_MEDIUM_FONT);
    }

    @Provides
    @Singleton
    public LayoutInflater provideLayoutInflater(@ForActivity Context context) {
        return LayoutInflater.from(context);
    }

    @Provides
    InputMethodManager provideInputMethodManager(@ForActivity Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Provides
    FragmentManager provideFramentManager() {
        return mActivity.getSupportFragmentManager();
    }

}
