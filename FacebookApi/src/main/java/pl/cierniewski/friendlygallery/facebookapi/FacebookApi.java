package pl.cierniewski.friendlygallery.facebookapi;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.util.Key;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.JsonSyntaxException;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.net.ssl.SSLException;

import pl.cierniewski.friendlygallery.facebookapi.content.GsonHttpContent;
import pl.cierniewski.friendlygallery.facebookapi.model.GetAlbumsResponse;
import pl.cierniewski.friendlygallery.facebookapi.model.GetFriendsResponse;
import pl.cierniewski.friendlygallery.facebookapi.model.GetPhotosResponse;

import static com.google.common.base.Preconditions.checkNotNull;

public class FacebookApi {
    /**
     * This validator have to be called only after processing entity
     */
    public static final String VALIDATOR_PROFILE_AFTER_PROCESS = "after_process";

    @Inject
    HttpRequestFactory mRequestFactory;
    @Inject
    @NoParserRequestFactory
    HttpRequestFactory mStringRequestFactory;
    @Inject
    Server mServer;
    @Inject
    @PreValidator
    Validator mPreValidator;
    @Inject
    Validator mValidator;
    @Inject
    Provider<GsonHttpContent> mGsonHttpContentProvider;
    @Inject
    AuthTokenFactory mAuthTokenFactory;

    public FacebookApi() {
    }

    public static class ValidationException extends IOException {

        public ValidationException(String detailMessage) {
            super(detailMessage);
        }
    }

    public abstract class Request {

        private final String mUriTemplate;
        private final String mRequestMethod;
        private HttpContent mContent;

        public Request(String requestMethod, String uriTemplate) {
            mRequestMethod = checkNotNull(requestMethod);
            mUriTemplate = checkNotNull(uriTemplate, "Uri could not be null");
        }

        public void setContent(HttpContent content) {
            mContent = content;
        }

        protected void setupRequest(HttpRequest httpRequest) {
        }

        protected HttpResponse executeRequest() throws IOException {
            final GenericUrl genericUrl = new GenericUrl(
                    UriTemplate.expand(mServer.getBaseUrl(), mUriTemplate, this, true));
            final HttpRequest httpRequest = getRequestFactory().buildRequest(mRequestMethod, genericUrl,
                    mContent);
            setupRequest(httpRequest);
            return httpRequest.execute();
        }

        protected abstract HttpRequestFactory getRequestFactory();
    }

    public abstract class BaseEmptyRequest extends Request {
        public BaseEmptyRequest(String requestMethod, String uriTemplate) {
            super(requestMethod, uriTemplate);
        }

        public void execute() throws IOException {
            executeRequest().ignore();
        }

        @Override
        protected HttpRequestFactory getRequestFactory() {
            return mStringRequestFactory;
        }
    }

    public abstract class BaseFileRequest extends Request {

        private File mFilePath;

        public BaseFileRequest(String fileUri, File filePath) {
            super(HttpMethods.GET, fileUri);
            mFilePath = checkNotNull(filePath);
        }

        public File execute() throws IOException {
            final HttpResponse httpResponse = executeRequest();
            try {
                final FileOutputStream fileOutputStream = new FileOutputStream(mFilePath);
                try {
                    httpResponse.download(fileOutputStream);
                    return mFilePath;
                } finally {
                    fileOutputStream.close();
                }
            } finally {
                httpResponse.disconnect();
            }
        }

        @Override
        protected HttpRequestFactory getRequestFactory() {
            return mStringRequestFactory;
        }

    }

    public abstract class BaseRequest<T> extends Request {

        private final Class<T> mResponseClass;

        public BaseRequest(String requestMethod, String uriTemplate,
                           Object content, Class<T> responseClass) {
            super(requestMethod, uriTemplate);
            mResponseClass = checkNotNull(responseClass);
            setObjectContent(content);
        }

        public void setObjectContent(Object content) {
            setContent(content == null ? null : getGsonHttpContent().setObject(content));
        }

        public GsonHttpContent getGsonHttpContent() {
            return mGsonHttpContentProvider.get();
        }

        @Override
        protected HttpRequestFactory getRequestFactory() {
            return mRequestFactory;
        }

        public T execute() throws IOException {
            try {
                return process(executeRequest().parseAs(mResponseClass));
            } catch (JsonSyntaxException e) {
                final Throwable cause = e.getCause();
                Throwables.propagateIfInstanceOf(cause, SocketTimeoutException.class);
                Throwables.propagateIfInstanceOf(cause, SSLException.class);
                Throwables.propagateIfInstanceOf(cause, InterruptedIOException.class);
                throw e;
            }
        }

        protected T process(T data) throws IOException {
            final boolean isVoid = Void.class.equals(mResponseClass);
            if (!isVoid && data == null) {
                throw new ValidationException("Data could not be null");
            }
            if (ProcessableType.class.isAssignableFrom(mResponseClass)) {
                /**
                 * If response class implements {@link ProcessableType} we need:
                 * - validate its content without
                 * {@link FacebookApi.VALIDATOR_PROFILE_AFTER_PROCESS} profile
                 * - process response and validate fields
                 */
                throwExceptionWhenInvalid(mPreValidator.validate(data));
                final ProcessableType processable = (ProcessableType) data;
                processable.process();
            }
            if (!isVoid) {
                /**
                 * We need validate response if its not {@link Void} instance;
                 */
                throwExceptionWhenInvalid(mValidator.validate(data));
            }
            return data;
        }

        private void throwExceptionWhenInvalid(List<ConstraintViolation> validate) throws ValidationException {
            if (validate.size() > 0) {
                final StringBuilder sb = new StringBuilder();
                for (ConstraintViolation validation : validate) {
                    validationToString(sb, validation);
                }
                throw new ValidationException(sb.toString());
            }
        }

        private void validationToString(StringBuilder sb, ConstraintViolation validation) {
            sb.append(validation.getMessage());
            final ConstraintViolation[] causes = validation.getCauses();
            if (causes != null && causes.length > 0) {
                sb.append(" (");
                for (ConstraintViolation cause : causes) {
                    validationToString(sb, cause);
                }
                sb.append(")");
            }
            sb.append(",");
        }

        public ListenableFuture<T> executeOnExecutor(ListeningExecutorService executor) {
            return executor.submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return execute();
                }
            });
        }
    }

    public class FacebookRequest<T> extends BaseRequest<T> {
        @Key
        String access_token;
        @Key
        String limit;
        @Key
        String format;
        @Key
        String pretty;
        @Key
        String suppress_http_code;

        public FacebookRequest(String requestMethod, String uriTemplate, Object content, Class<T> responseClass, int limit) {
            super(requestMethod, uriTemplate, content, responseClass);

            this.access_token = mAuthTokenFactory.getAuthToken();
            this.limit = String.valueOf(limit);

            this.format = "json";
            this.pretty = "0";
            this.suppress_http_code = "1";
        }
    }


    public GetFriends getFriends(String friendId, int limit) {
        return new GetFriends(friendId, limit);
    }

    public GetPhotos getPhotos(String friendOrAlbumId, int limit) {
        return new GetPhotos(friendOrAlbumId, limit);
    }

    public GetAlbums getAlbums(String friendId, int limit) {
        return new GetAlbums(friendId, limit);
    }

    public class GetFriends extends FacebookRequest<GetFriendsResponse> {

        private static final String REST_PATH = "v1.0/{friendId}/friends";

        @Key
        String friendId;

        private GetFriends(String friendId, int limit) {
            super(HttpMethods.GET, REST_PATH, null, GetFriendsResponse.class, limit);
            this.friendId = friendId;
        }
    }

    public class GetPhotos extends FacebookRequest<GetPhotosResponse> {

        private static final String REST_PATH = "v1.0/{friendId}/photos";

        @Key
        String friendId;

        private GetPhotos(String friendId, int limit) {
            super(HttpMethods.GET, REST_PATH, null, GetPhotosResponse.class, limit);
            this.friendId = friendId;
        }
    }

    public class GetAlbums extends FacebookRequest<GetAlbumsResponse> {

        private static final String REST_PATH = "v1.0/{friendId}/albums";

        @Key
        String friendId;

        private GetAlbums(String friendId, int limit) {
            super(HttpMethods.GET, REST_PATH, null, GetAlbumsResponse.class, limit);
            this.friendId = friendId;
        }
    }
}
