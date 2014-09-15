package pl.cierniewski.friendlygallery;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import pl.cierniewski.friendlygallery.dagger.ForActivity;

public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";
    private static final List<String> PERMISSIONS = Arrays.asList("read_friendlists",
            "user_friends", "user_photos", "user_relationships", "user_photo_video_tags");

    @Inject
    @ForActivity
    Context mContext;
    @Inject
    ConnectivityManager mConnectivityManager;

    @InjectView(R.id.home_update_photos_button)
    Button mUpdatePhotosButton;
    @InjectView(R.id.facebook_login_button)
    LoginButton mLoginButton;

    private UiLifecycleHelper mUiLifecycleHelper;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUiLifecycleHelper = new UiLifecycleHelper(getActivity(), statusCallback);
        mUiLifecycleHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoginButton.setFragment(this);
        mLoginButton.setReadPermissions(PERMISSIONS);
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            mUpdatePhotosButton.setVisibility(View.VISIBLE);
        } else if (state.isClosed()) {
            mUpdatePhotosButton.setVisibility(View.GONE);
        }
    }

//    @OnClick(R.id.facebook_login_button)
//    public void onLoginFacebookClicked() {
//        final Session session = Session.getActiveSession();
//        if (!session.isOpened() && !session.isClosed()) {
//            session.openForRead(new Session.OpenRequest(this)
//                    .setPermissions(PERMISSIONS)
//                    .setCallback(statusCallback));
//        } else {
//            Session.openActiveSession(getActivity(), this, true, statusCallback);
//        }
//    }

    @OnClick(R.id.home_update_photos_button)
    public void onUpdatePhotosClicked() {
        if (mConnectivityManager.getActiveNetworkInfo() == null) {
            Toast.makeText(mContext, "There is no internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        final Session session = Session.getActiveSession();
        Log.i(TAG, session.getPermissions().toString());

        new Request(
                session,
                "/me/friends",
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                            /* handle the result */
                        Log.e(TAG, response.getRawResponse());
                    }
                }
        ).executeAsync();
    }

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        mUiLifecycleHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        mUiLifecycleHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUiLifecycleHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUiLifecycleHelper.onSaveInstanceState(outState);
    }
}
