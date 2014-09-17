package pl.cierniewski.friendlygallery.facebookapi.exceptions;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.common.base.Optional;

import java.util.Locale;

public abstract class ServerException extends HttpResponseException {

    public static class ErrorMessage {

        private final String mResponse;
        private final Optional<String> mTitle;
        private final Optional<String> mDescription;

        public ErrorMessage(String response, Optional<String> title, Optional<String> description) {
            mResponse = response;
            mTitle = title;
            mDescription = description;
        }

        public ErrorMessage(String response) {
            this(response, Optional.<String>absent(), Optional.<String>absent());
        }

        public Optional<String> getTitle() {
            return mTitle;
        }
    }

    private static final long serialVersionUID = 1L;


    private final ErrorMessage mErrorMessage;

    public ServerException(HttpResponse response, ErrorMessage errorMessage) {
        super(response);
        mErrorMessage = errorMessage;
    }

    public Optional<String> getDescription() {
        return mErrorMessage.mDescription;
    }

    public Optional<String> getTitle() {
        return mErrorMessage.mTitle;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    @Override
    public String getMessage() {
        final Optional<String> description = getDescription();
        if (description.isPresent()) {
            return String.format(Locale.US, "Error %d %s (description: %s)",
                    getStatusCode(), getStatusMessage(), description.get());
        } else {
            return String.format(Locale.US, "Error %d %s (response: %s)",
                    getStatusCode(), getStatusMessage(), mErrorMessage.mResponse);
        }
    }

}
