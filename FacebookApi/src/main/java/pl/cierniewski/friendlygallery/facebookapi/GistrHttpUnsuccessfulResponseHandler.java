package pl.cierniewski.friendlygallery.facebookapi;

import pl.cierniewski.friendlygallery.facebookapi.exceptions.ForbiddenServerException;
import pl.cierniewski.friendlygallery.facebookapi.exceptions.NotFoundServerException;
import pl.cierniewski.friendlygallery.facebookapi.exceptions.ServerException;
import pl.cierniewski.friendlygallery.facebookapi.exceptions.UnauthorizedServerException;
import pl.cierniewski.friendlygallery.facebookapi.exceptions.UnknownServerException;
import pl.cierniewski.friendlygallery.facebookapi.exceptions.UnprocessableEntityException;
import pl.cierniewski.friendlygallery.facebookapi.exceptions.UnsupportedVersionException;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.common.base.Optional;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

class GistrHttpUnsuccessfulResponseHandler extends ChainHttpUnsuccessfulResponseHandler {


    @Inject
    GistrHttpUnsuccessfulResponseHandler() {
    }

    @Override
    public boolean handleResponse(HttpRequest httpRequest, HttpResponse httpResponse, boolean supportsRetry)
            throws IOException {
        final int statusCode = httpResponse.getStatusCode();
        final ServerException.ErrorMessage errorMessage = getErrorMessage(httpResponse);

        final Optional<String> title = errorMessage.getTitle();
        String titleMessage;
        if (title.isPresent()) {
            titleMessage = title.get();
        } else {
            titleMessage = "";
        }

        if (statusCode == HttpStatus.SC_NOT_FOUND) {
            throw new NotFoundServerException(httpResponse, errorMessage);
        } else if (statusCode == HttpStatus.SC_BAD_REQUEST || statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
            throw new UnprocessableEntityException(httpResponse, errorMessage);
        } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new UnauthorizedServerException(httpResponse, errorMessage);
        } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
            if (titleMessage.equals("Unsupported version")) {       // FIXME Should has other statusCode
                throw new UnsupportedVersionException(httpResponse, errorMessage);
            } else {
                throw new ForbiddenServerException(httpResponse, errorMessage);
            }
        } else {
            throw new UnknownServerException(httpResponse, errorMessage);
        }
    }

    private boolean isJsonContentType(HttpResponse httpResponse) {
        final HttpMediaType mediaType = httpResponse.getMediaType();
        return mediaType != null && "application".equals(mediaType.getType()) && "json".equals(mediaType.getSubType());
    }

    private ServerException.ErrorMessage getErrorMessage(HttpResponse httpResponse) throws IOException {
        final String response = httpResponse.parseAsString();
        if (isJsonContentType(httpResponse)) {
            try {
                final JSONObject json = new JSONObject(response);
                final Optional<String> title;
                final Optional<String> description;
                if (json.has("description")) {
                    description = Optional.fromNullable(json.getString("description"));
                } else {
                    description = Optional.absent();
                }
                if (json.has("title")) {
                    title = Optional.fromNullable(json.getString("title"));
                } else {
                    title = Optional.absent();
                }
                return new ServerException.ErrorMessage(response, title, description);
            } catch (JSONException ignore) {}
        }

        return new ServerException.ErrorMessage(response);
    }
}
