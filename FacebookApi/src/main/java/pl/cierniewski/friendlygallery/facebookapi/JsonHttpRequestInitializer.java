package pl.cierniewski.friendlygallery.facebookapi;

import pl.cierniewski.friendlygallery.facebookapi.content.GsonObjectParser;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.gson.Gson;

import javax.inject.Inject;
import java.io.IOException;

public class JsonHttpRequestInitializer extends GistrHttpRequestInitializer {

    @Inject
    Gson gson;

    @Inject
    public JsonHttpRequestInitializer() {
    }

    @Override
    public void initialize(HttpRequest request) throws IOException {
        super.initialize(request);
        final HttpHeaders headers = request.getHeaders();
        headers.setAccept("application/json");

        request.setParser(new GsonObjectParser(gson));
    }

}
