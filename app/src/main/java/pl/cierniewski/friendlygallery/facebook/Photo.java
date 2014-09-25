package pl.cierniewski.friendlygallery.facebook;

import com.google.common.base.Function;

import java.util.List;

import javax.annotation.Nullable;

import pl.cierniewski.friendlygallery.facebookapi.model.ImageType;
import pl.cierniewski.friendlygallery.facebookapi.model.PhotoType;
import pl.cierniewski.friendlygallery.facebookapi.model.TagType;

public class Photo {

    public String id;
    public String name;

    public String source;
    public int width;
    public int height;

    public List<TagType> tags;

    public String fromId;
    public String fromName;

    public String createdTime;

    public static Function<PhotoType, Photo> convertPhotoFunction = new Function<PhotoType, Photo>() {
        @Nullable
        @Override
        public Photo apply(@Nullable PhotoType input) {
            if (input == null) return null;

            final Photo photo = new Photo();
            photo.id = input.id;
            photo.name = input.name;

            int maxWidth = 0;
            ImageType maxImage = null;
            for (ImageType image : input.images) {
                if (image.width > maxWidth) {
                    maxWidth = image.width;
                    maxImage = image;
                }
            }
            if (maxImage != null) {
                photo.source = maxImage.source;
                photo.width = maxImage.width;
                photo.height = maxImage.height;
            }

            if (input.tags != null) {
                photo.tags = input.tags.data;
            }

            if (input.from != null) {
                photo.fromId = input.from.id;
                photo.fromName = input.from.name;
            }

            photo.createdTime = input.id;

            return photo;
        }
    };
}
