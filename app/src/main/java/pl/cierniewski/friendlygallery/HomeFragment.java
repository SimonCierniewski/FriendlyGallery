package pl.cierniewski.friendlygallery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import pl.cierniewski.friendlygallery.dagger.ForActivity;
import pl.cierniewski.friendlygallery.facebook.Album;
import pl.cierniewski.friendlygallery.facebook.FacebookDataCollector;
import pl.cierniewski.friendlygallery.facebook.Friend;
import pl.cierniewski.friendlygallery.facebook.Photo;

public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";
    private static final List<String> PERMISSIONS = Arrays.asList("read_friendlists",
            "user_friends", "user_photos", "user_relationships", "user_photo_video_tags");

    @Inject
    @ForActivity
    Context mContext;
    @Inject
    ConnectivityManager mConnectivityManager;
    @Inject
    FacebookDataCollector mFacebookDataCollector;
    @Inject
    Gson mGson;
    @Inject
    ListeningExecutorService mExecutor;

    @InjectView(R.id.facebook_login_button)
    LoginButton mLoginButton;

    private File mMainDirectory;
    private UiLifecycleHelper mUiLifecycleHelper;



    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mUiLifecycleHelper = new UiLifecycleHelper(getActivity(), statusCallback);
//        mUiLifecycleHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        mMainDirectory = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        mMainDirectory = new File("/mnt/shared/GENYMOTION");
    }

    /* TODO Uncomment Facebook Login
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
                        Log.e(TAG, response.getRawResponse());
                    }
                }
        ).executeAsync();
    }*/

    @OnClick(R.id.home_get_facebook_structure_button)
    public void onGetFacebookStructureClicked() {

        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {

                    final List<Friend> friends = mFacebookDataCollector.collectFriends("me", 1000);

                    final Friend me = new Friend("633148500136430", "Szymon Cierniewski");
                    friends.add(me);

                    int i = 0;
                    for (Friend friend : friends) {
//                    final Friend friend = friends.get(37);
                        friend.albums = mFacebookDataCollector.collectAlbumsWithPhotos(friend.id, 5000);

                        final String friendData = mGson.toJson(friend);

                        final String fileName = String.format("%s_%s.txt", friend.id, friend.name.replace(' ', '_'));
                        Files.write(friendData, mMainDirectory, Charsets.UTF_8);

                        i++;
                        Log.d(TAG, i + " FACEBOOK DATA COLLECTED: " + friend.name);
                    }

                    Log.e(TAG, "FACEBOOK DATA COLLECTED");

                } catch (IOException e) {
                    Log.e(TAG, "Error while collecting data: " + e.toString());
                }
            }
        });
    }

    @OnClick(R.id.home_available_space_button)
    public void onGetAvailableSpaceClicked() {
        final long availableBytes = mMainDirectory.getFreeSpace();
        final long availableMegaBytes = availableBytes / 1024 / 1024;
        Log.d(TAG, String.format("Available %d MB", availableMegaBytes));
    }

    @OnClick(R.id.home_download_photos_button)
    public void onDownloadPhotosClicked() {

        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final File structureDir = new File(mMainDirectory, "structure");
                    for (File file : structureDir.listFiles()) {
                        final String stringFromFile = Files.asCharSource(file, Charsets.UTF_8).read();
                        final Friend friend = mGson.fromJson(stringFromFile, Friend.class);

                        final long time = System.currentTimeMillis();

                        int downloadedCount = 0;
                        final int photosCount = friend.getPhotosCount();
                        Log.e(TAG, String.format("%s has %d photos", friend.name, photosCount));

                        if (photosCount == 0) {
                            continue;
                        }

                        final File friendDir = new File(mMainDirectory, String.format("/photos/%s_%s", friend.id, friend.name.replace(' ', '_')));

                        for (Album album : friend.albums) {
                            final File albumDir = new File(friendDir, String.format("%s_%s-%s", album.id, album.type,
                                    album.name.trim().replace(' ', '_').replace('/', '_')));
                            if (!albumDir.exists()) {
                                if (!albumDir.mkdirs()) {
                                Log.e(TAG, "ERROR Can't create directory: " + albumDir.toString());
                                continue;
                                }
                            }

                            for (Photo photo : album.photos) {
                                try {
                                    final File photoFile;
                                    if (photo.source.contains(".jpg")) {
                                        photoFile = new File(albumDir, String.format("%s.jpg", photo.id));
                                    } else if (photo.source.contains(".png")) {
                                        photoFile = new File(albumDir, String.format("%s.png", photo.id));
                                    } else if (photo.source.contains(".gif")) {
                                        photoFile = new File(albumDir, String.format("%s.gif", photo.id));
                                    } else {
                                        photoFile = new File(albumDir, photo.id);
                                        Log.e(TAG, "UNKNOWN File type: " + photo.source);
                                    }

                                    if (photoFile.exists()) {
                                        continue;
                                    }

                                    Log.d(TAG, String.format("Downloading... %s - %s", friend.name, photoFile.getPath()));

                                    final URL url = new URL(photo.source);
                                    final byte[] data = Resources.toByteArray(url);

                                    final OutputStream os = new FileOutputStream(photoFile);
                                    try {
                                        os.write(data);
                                    } finally {
                                        os.close();
                                    }

                                    if (downloadedCount % 10 == 0) {
                                        Log.d(TAG, String.format("%s - %d / %d", friend.name, ++downloadedCount, photosCount));
                                    }

                                } catch (IOException e) {
                                    Log.e(TAG, String.format("ERROR %s -> %s", photo.source, e.toString()));
                                }
                            }

                            Log.d(TAG, "Photos downloaded for album: " + album.id);
                        }

                        Log.d(TAG, String.format("PHOTOS DOWNLOADED for: %s - %d - %d ms", friend.name,
                                photosCount, (System.currentTimeMillis() - time) / photosCount));
                    }

                    Log.d(TAG, "FACEBOOK DATA COLLECTED");
                } catch (IOException e) {
                    Log.e(TAG, "ERROR while collecting data: " + e.toString());
                }
            }
        });
    }

    /* TODO Uncomment Facebook Login
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
    }*/
}
