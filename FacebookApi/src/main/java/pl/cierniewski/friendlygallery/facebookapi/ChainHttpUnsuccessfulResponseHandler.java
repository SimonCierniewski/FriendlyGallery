package pl.cierniewski.friendlygallery.facebookapi;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;

import java.io.IOException;

abstract class ChainHttpUnsuccessfulResponseHandler implements HttpUnsuccessfulResponseHandler {

    private HttpUnsuccessfulResponseHandler mNextHandler;

    ChainHttpUnsuccessfulResponseHandler() {
    }

    public boolean callNext(HttpRequest httpRequest, HttpResponse response, boolean supportsRetry) throws IOException {
        return mNextHandler != null && mNextHandler.handleResponse(httpRequest, response, supportsRetry);
    }


    public void setNextHandler(HttpUnsuccessfulResponseHandler nextHandler) {
        mNextHandler = nextHandler;
    }
}
