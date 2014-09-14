package pl.cierniewski.friendlygallery;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import dagger.ObjectGraph;
import pl.cierniewski.friendlygallery.dagger.ActivityGraphProvider;
import pl.cierniewski.friendlygallery.dagger.ActivityModule;

public abstract class BaseActivity extends ActionBarActivity implements ActivityGraphProvider {

    private ObjectGraph mActivityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityGraph().inject(this);
    }

    @Override
    public ObjectGraph getActivityGraph() {
        if (mActivityGraph == null) {
            mActivityGraph = MainApplication.fromApplication(getApplication())
                    .getApplicationGraph()
                    .plus(new ActivityModule(this));
        }

        return mActivityGraph;
    }
}
