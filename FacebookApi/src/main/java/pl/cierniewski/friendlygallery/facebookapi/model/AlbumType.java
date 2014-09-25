package pl.cierniewski.friendlygallery.facebookapi.model;

import net.sf.oval.constraint.NotNull;

public class AlbumType {

    @NotNull
    public String id;
    public String name;
    @NotNull
    public String type;

    public int count;

    public AlbumType(AlbumType albumType) {
        this.id = albumType.id;
        this.name = albumType.name;
        this.type = albumType.type;
        this.count = albumType.count;
    }

    public AlbumType(String id, String name, String type, int count) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.count = count;
    }
}
