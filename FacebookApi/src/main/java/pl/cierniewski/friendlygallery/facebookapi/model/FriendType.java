package pl.cierniewski.friendlygallery.facebookapi.model;

import net.sf.oval.constraint.NotNull;

public class FriendType {

    @NotNull
    public String id;
    @NotNull
    public String name;

    public FriendType(FriendType friendType) {
        this.id = friendType.id;
        this.name = friendType.name;
    }

    public FriendType(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
