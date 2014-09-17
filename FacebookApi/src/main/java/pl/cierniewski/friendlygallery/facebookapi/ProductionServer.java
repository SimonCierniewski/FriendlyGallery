package pl.cierniewski.friendlygallery.facebookapi;

public class ProductionServer extends Server {
    @Override
    public String getBaseUrl() {
        return "https://graph.facebook.com/";
    }
}
