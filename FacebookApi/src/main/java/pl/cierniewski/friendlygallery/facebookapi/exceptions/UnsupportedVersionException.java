package pl.cierniewski.friendlygallery.facebookapi.exceptions;

import com.google.api.client.http.HttpResponse;

public class UnsupportedVersionException extends ServerException {

    private static final long serialVersionUID = 1L;

    public UnsupportedVersionException(HttpResponse response, ErrorMessage errorMessage) {
        super(response, errorMessage);
    }
}
