package pl.cierniewski.friendlygallery.facebookapi.exceptions;

import com.google.api.client.http.HttpResponse;

public class NotFoundServerException extends ServerException {

    private static final long serialVersionUID = 1L;

    public NotFoundServerException(HttpResponse response, ErrorMessage errorMessage) {
        super(response, errorMessage);
    }
}
