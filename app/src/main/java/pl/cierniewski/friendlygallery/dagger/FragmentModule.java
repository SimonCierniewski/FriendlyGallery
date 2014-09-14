package pl.cierniewski.friendlygallery.dagger;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.cierniewski.friendlygallery.BaseActivity;
import pl.cierniewski.friendlygallery.BaseFragment;
import pl.cierniewski.friendlygallery.HomeFragment;

@Module(
        injects = {
                HomeFragment.class
        },
        addsTo = ActivityModule.class,
        overrides = true,
        library = true
)
public class FragmentModule {

    BaseFragment mFragment;

    public FragmentModule(BaseFragment fragment) {
        mFragment = fragment;
    }

    @Provides
    @Singleton
    public ActionBar provideSupportActionBar() {
        return ((BaseActivity) mFragment.getActivity()).getSupportActionBar();
    }

    @Provides
    @Singleton
    public BaseFragment provideBaseFragment() {
        return mFragment;
    }

    @Provides
    FragmentManager provideFragmentManager() {
        return mFragment.getChildFragmentManager();
    }
}
