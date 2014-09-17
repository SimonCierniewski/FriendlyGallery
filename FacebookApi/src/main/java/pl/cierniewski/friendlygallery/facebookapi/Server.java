package pl.cierniewski.friendlygallery.facebookapi;

public abstract class Server {
    public abstract String getBaseUrl();

    public String getVersion() {
        return "1";
    }
}
