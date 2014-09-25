package pl.cierniewski.friendlygallery.facebookapi.model;

import net.sf.oval.constraint.AssertValid;

import java.util.List;

public class GetAlbumsResponse {

    @AssertValid
    public List<AlbumType> data;

}
