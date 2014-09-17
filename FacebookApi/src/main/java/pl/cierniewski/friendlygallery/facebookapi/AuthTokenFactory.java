package pl.cierniewski.friendlygallery.facebookapi;

public interface AuthTokenFactory {
    public static final String NOT_LOGGED_IN = null;

    String getAuthToken();
}
