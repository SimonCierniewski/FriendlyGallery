package pl.cierniewski.friendlygallery.facebookapi.model;

import net.sf.oval.constraint.NotNull;

public class AlbumType {

    @NotNull
    public String id;
    public String name;
    @NotNull
    public String type;

    public int count;
}
