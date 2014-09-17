package pl.cierniewski.friendlygallery.facebookapi.content;

import com.google.api.client.util.ObjectParser;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

public class GsonObjectParser implements ObjectParser {

    private final Gson mGson;

    public GsonObjectParser(Gson gson) {
        mGson = checkNotNull(gson);
    }

    @Override
    public <T> T parseAndClose(InputStream inputStream, Charset charset, Class<T> tClass) throws IOException {
        checkNotNull(tClass, "tClass could not be null");
        checkNotNull(charset, "charset could not be null");
        if (inputStream == null) return null;
        try {
            final InputStreamReader reader = new InputStreamReader(inputStream, charset);
            return parseAndClose(reader, tClass);
        } finally {
            inputStream.close();
        }
    }

    @Override
    public Object parseAndClose(InputStream inputStream, Charset charset, Type type) throws IOException {
        checkNotNull(type, "type could not be null");
        checkNotNull(charset, "charset could not be null");
        if (inputStream == null) return null;
        try {
            final InputStreamReader reader = new InputStreamReader(inputStream, charset);
            return parseAndClose(reader, type);
        } finally {
            inputStream.close();
        }
    }

    @Override
    public <T> T parseAndClose(Reader reader, Class<T> tClass) throws IOException {
        checkNotNull(tClass, "tClass could not be null");
        if (reader == null) return null;
        try {
            return mGson.fromJson(reader, tClass);
        } finally {
            reader.close();
        }
    }

    @Override
    public Object parseAndClose(Reader reader, Type type) throws IOException {
        checkNotNull(type, "type could not be null");
        if (reader == null) return null;
        try {
            return mGson.fromJson(reader, type);
        } finally {
            reader.close();
        }
    }
}
