package pl.cierniewski.friendlygallery.facebookapi.model;

import net.sf.oval.constraint.NotNull;

public class FriendType {

    @NotNull
    public String id;
    @NotNull
    public String name;
}
