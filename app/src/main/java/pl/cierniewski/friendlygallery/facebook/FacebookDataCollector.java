package pl.cierniewski.friendlygallery.facebook;

import android.util.Log;

import com.google.api.client.util.Sets;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;

import pl.cierniewski.friendlygallery.facebookapi.FacebookApi;
import pl.cierniewski.friendlygallery.facebookapi.model.GetAlbumsResponse;
import pl.cierniewski.friendlygallery.facebookapi.model.GetFriendsResponse;
import pl.cierniewski.friendlygallery.facebookapi.model.GetPhotosResponse;
import pl.cierniewski.friendlygallery.helper.LogHelper;

public class FacebookDataCollector {

    private static final String UNKNOWN = "unknown";

    private static final String TAG = "FacebookDataCollector";

    @Inject
    FacebookApi mFacebookApi;

    private Logger mLogger = LogHelper.getFacebookDataCollectorLogger();

    @Inject
    public FacebookDataCollector() {
    }

    public List<Album> collectAlbumsWithPhotos(String friendId, int limit) throws IOException {
        final List<Album> albums = Lists.newArrayList();
        albums.addAll(collectAlbums(friendId, limit));

        final Set<String> photosInAlbumsIds = Sets.newHashSet();

        for (Album album : albums) {
            final List<Photo> photos = collectPhotos(album.id, limit);
            album.photos = photos;

            for (Photo photo : photos) {
                photosInAlbumsIds.add(photo.id);
            }
        }

        final List<Photo> photosOutsizeAlbums = Lists.newArrayList();
        final List<Photo> friendPhotos = collectPhotos(friendId, limit);

        for (Photo friendPhoto : friendPhotos) {
            if (!photosInAlbumsIds.contains(friendPhoto.id)) {
                photosOutsizeAlbums.add(friendPhoto);
            }
        }

        if (photosOutsizeAlbums.size() > 0) {
            final Album unknownAlbum = new Album(UUID.randomUUID().toString(),
                    "Unknown album", UNKNOWN, friendPhotos.size());
            unknownAlbum.photos = photosOutsizeAlbums;
            albums.add(unknownAlbum);
        }

        return albums;
    }


    public List<Friend> collectFriends(String friendId, int limit) throws IOException {
        final GetFriendsResponse response = mFacebookApi.getFriends(friendId, limit).execute();
        final List<Friend> friends = Lists.transform(response.data, Friend.convertFriendFunction);

        mLogger.finer(String.format("FRIENDS - size: %d", friends.size()));
        return friends;
    }

    private List<Album> collectAlbums(String friendId, int limit) throws IOException {
        final GetAlbumsResponse response = mFacebookApi.getAlbums(friendId, limit).execute();
        final List<Album> albums = Lists.transform(response.data, Album.convertAlbumFunction);

        mLogger.finer(String.format("ALBUMS - size: %d - FriendID: %s", albums.size(), friendId));
        return albums;
    }

    private List<Photo> collectPhotos(String friendOrAlbumId, int limit) throws IOException {
        final List<Photo> photos = Lists.newArrayList();

        // FIXME Add LOAD MORE - parameter "until" in request
//        while (true) {
            final GetPhotosResponse response = mFacebookApi.getPhotos(friendOrAlbumId, limit).execute();
            photos.addAll(Lists.transform(response.data, Photo.convertPhotoFunction));

//            if (response.data.size() < limit) break;
            if (response.data.size() >= limit) {
                Log.e(TAG, "XXXXXXXXXXXXXXXXXXXXXXX LIMIT XXXXXXXXXXXXXXXXXXXXXXX " + friendOrAlbumId);
                return photos;
            }
//        }

        mLogger.finer(String.format("PHOTOS - size: %d - ID: %s", photos.size(), friendOrAlbumId));
        return photos;
    }
}
