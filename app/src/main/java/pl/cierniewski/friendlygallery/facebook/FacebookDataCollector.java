package pl.cierniewski.friendlygallery.facebook;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import pl.cierniewski.friendlygallery.facebookapi.FacebookApi;
import pl.cierniewski.friendlygallery.facebookapi.model.GetAlbumsResponse;
import pl.cierniewski.friendlygallery.facebookapi.model.GetFriendsResponse;
import pl.cierniewski.friendlygallery.facebookapi.model.GetPhotosResponse;
import pl.cierniewski.friendlygallery.helper.LogHelper;

public class FacebookDataCollector {

    @Inject
    FacebookApi mFacebookApi;

    private Logger mLogger = LogHelper.getFacebookDataCollectorLogger();

    @Inject
    public FacebookDataCollector() {
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
//        }

        mLogger.finer(String.format("PHOTOS - size: %d - ID: %s", photos.size(), friendOrAlbumId));
        return photos;
    }
}
