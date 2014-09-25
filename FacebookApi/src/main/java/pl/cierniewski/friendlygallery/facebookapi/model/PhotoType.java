package pl.cierniewski.friendlygallery.facebookapi.model;

import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.MinSize;
import net.sf.oval.constraint.NotNull;

import java.util.List;

public class PhotoType {

    @NotNull
    public String id;
    public String name;

    @AssertValid
    public FriendType from;

    @AssertValid
    @MinSize(1)
    public List<ImageType> images;

//    @AssertValid
    public TagsType tags;

    public String createdTime;
}
