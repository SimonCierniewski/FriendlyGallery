package pl.cierniewski.friendlygallery.facebook;

import com.google.common.base.Function;

import java.util.List;

import javax.annotation.Nullable;

import pl.cierniewski.friendlygallery.facebookapi.model.FriendType;

public class Friend extends FriendType {

    public List<Album> albums;

    public Friend(FriendType friendType) {
        super(friendType);
    }

    public Friend(String id, String name) {
        super(id, name);
    }

    public static Function<FriendType, Friend> convertFriendFunction = new Function<FriendType, Friend>() {
        @Nullable
        @Override
        public Friend apply(@Nullable FriendType input) {
            return new Friend(input);
        }
    };

    public int getPhotosCount() {
        int count = 0;
        for (Album album : albums) {
            count += album.photos != null ? album.photos.size() : 0;
        }
        return count;
    }
}
