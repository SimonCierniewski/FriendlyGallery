package pl.cierniewski.friendlygallery.facebookapi.model;

import net.sf.oval.constraint.AssertValid;

import java.util.List;

public class TagsType {

    @AssertValid
    public List<TagType> data;
}
