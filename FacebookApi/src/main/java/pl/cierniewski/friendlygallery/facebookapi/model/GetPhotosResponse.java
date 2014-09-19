package pl.cierniewski.friendlygallery.facebookapi.model;

import net.sf.oval.constraint.AssertValid;

import java.util.List;

public class GetPhotosResponse {

    @AssertValid
    public List<PhotoType> data;

}
