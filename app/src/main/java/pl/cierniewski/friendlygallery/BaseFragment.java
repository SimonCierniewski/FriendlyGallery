package pl.cierniewski.friendlygallery;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.google.common.collect.Lists;

import java.util.List;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import pl.cierniewski.friendlygallery.dagger.ActivityGraphProvider;
import pl.cierniewski.friendlygallery.dagger.FragmentModule;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseFragment extends DialogFragment {

    private ObjectGraph mFragmentGraph;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            final ActivityGraphProvider graphProvider = checkNotNull((ActivityGraphProvider) getActivity());
            mFragmentGraph = graphProvider.getActivityGraph().plus(getModules().toArray());
        } catch (ClassCastException e) {
            throw new RuntimeException("Activity does not implement ActivityGraphProvider", e);
        }

        mFragmentGraph.inject(this);
    }

    protected List<Object> getModules() {
        final Object fragmentModule = new FragmentModule(this);
        return Lists.newArrayList(fragmentModule);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }
}
