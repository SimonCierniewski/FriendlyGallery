package pl.cierniewski.friendlygallery.facebookapi.model;

import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.MinSize;

import java.util.List;

public class GetFriendsResponse {

    @AssertValid
    @MinSize(1)
    public List<FriendType> data;

}
