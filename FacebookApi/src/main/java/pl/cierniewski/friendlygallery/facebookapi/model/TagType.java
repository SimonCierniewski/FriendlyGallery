package pl.cierniewski.friendlygallery.facebookapi.model;

import net.sf.oval.constraint.NotNull;

public class TagType {

    @NotNull
    public String id;
    @NotNull
    public String name;

    @NotNull
    public double x;
    @NotNull
    public double y;
}
