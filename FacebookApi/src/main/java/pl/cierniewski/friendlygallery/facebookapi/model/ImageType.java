package pl.cierniewski.friendlygallery.facebookapi.model;

import net.sf.oval.constraint.NotNull;

public class ImageType {

    @NotNull
    public String source;
    @NotNull
    public int width;
    @NotNull
    public int height;
}
