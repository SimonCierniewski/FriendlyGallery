package pl.cierniewski.friendlygallery.facebookapi;

import pl.cierniewski.friendlygallery.facebookapi.content.GsonHttpContent;
import pl.cierniewski.friendlygallery.facebookapi.content.MemoryGsonHttpContent;
import pl.cierniewski.friendlygallery.facebookapi.parser.AndroidUnderscoreNamingStrategy;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.Sleeper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import net.sf.oval.Validator;

import javax.inject.Singleton;

@Module(
        injects = FacebookApi.class,
        complete = false,
        library = true
)
public class ApiModule {

    @Provides
    GsonHttpContent provideGsonHttpContent(Gson gson) {
        return new MemoryGsonHttpContent(gson);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .setFieldNamingStrategy(new AndroidUnderscoreNamingStrategy())
                .create();
    }

    @Provides
    @Singleton
    Validator provideValidator() {
        return new Validator();
    }

    @Provides
    @Singleton
    @PreValidator
    Validator providesPreValidator() {
        final Validator preValidator = new Validator();
        preValidator.disableProfile(FacebookApi.VALIDATOR_PROFILE_AFTER_PROCESS);
        return preValidator;
    }

    @Provides
    @Singleton
    HttpUnsuccessfulResponseHandler providesHttpUnsuccessfulResponseHandler() {
        final ExponentialBackOff backOff = new ExponentialBackOff.Builder().build();
        final ChainHttpUnsuccessfulResponseHandler handler =
                new BackOffHttpUnsuccessfulResponseHandler(backOff, Sleeper.DEFAULT);
        handler.setNextHandler(new GistrHttpUnsuccessfulResponseHandler());
        return handler;
    }

    @Provides
    @Singleton
    HttpRequestFactory providesHttpRequestFactory(HttpTransport httpTransport,
                                                  JsonHttpRequestInitializer httpRequestInitializer) {
        return httpTransport.createRequestFactory(httpRequestInitializer);
    }

    @NoParserRequestFactory
    @Provides
    @Singleton
    HttpRequestFactory providesStringHttpRequestFactory(HttpTransport httpTransport,
                                                        GistrHttpRequestInitializer httpRequestInitializer) {
        return httpTransport.createRequestFactory(httpRequestInitializer);
    }
}
