package pl.cierniewski.friendlygallery.facebook;

import com.google.common.base.Function;

import java.util.List;

import javax.annotation.Nullable;

import pl.cierniewski.friendlygallery.facebookapi.model.AlbumType;

public class Album extends AlbumType {

    public List<Photo> photos;

    public Album(AlbumType albumType) {
        super(albumType);
    }

    public Album(String id, String name, String type, int count) {
        super(id, name, type, count);
    }

    public static Function<AlbumType, Album> convertAlbumFunction = new Function<AlbumType, Album>() {
        @Nullable
        @Override
        public Album apply(@Nullable AlbumType input) {
            return new Album(input);
        }
    };
}
