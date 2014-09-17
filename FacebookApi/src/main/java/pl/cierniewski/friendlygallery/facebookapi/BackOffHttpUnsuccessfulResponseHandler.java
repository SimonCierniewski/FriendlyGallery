package pl.cierniewski.friendlygallery.facebookapi;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.BackOff;
import com.google.api.client.util.BackOffUtils;
import com.google.api.client.util.Sleeper;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

class BackOffHttpUnsuccessfulResponseHandler extends ChainHttpUnsuccessfulResponseHandler {

    private final BackOff mBackOff;
    private final Sleeper mSleeper;

    BackOffHttpUnsuccessfulResponseHandler(BackOff backOff, Sleeper sleeper) {
        mBackOff = checkNotNull(backOff);
        mSleeper = checkNotNull(sleeper);
    }

    @Override
    public boolean handleResponse(HttpRequest httpRequest,
                                  HttpResponse httpResponse,
                                  boolean supportsRetry) throws IOException {
        if (httpResponse.isSuccessStatusCode()) {
            mBackOff.reset();
            return false;
        }

        if (supportsRetry && isServerError(httpResponse)) {
            try {
                if (BackOffUtils.next(mSleeper, mBackOff)) {
                    return true;
                }
                // throw corresponding error
            } catch (InterruptedException exception) {
                // ignore
            }
        }

        return callNext(httpRequest, httpResponse, supportsRetry);
    }

    public boolean isServerError(HttpResponse httpResponse) {
        return httpResponse.getStatusCode() / 100 == 5;
    }

}
