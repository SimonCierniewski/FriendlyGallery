package pl.cierniewski.friendlygallery.facebookapi.content;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static com.google.common.base.Preconditions.checkNotNull;

public class StreamingGsonHttpContent extends GsonHttpContent {

    private static final int UNKNOWN_LENGTH = -1;

    public StreamingGsonHttpContent(Gson gson) {
        super(gson);
    }

    @Override
    public long getLength() throws IOException {
        return UNKNOWN_LENGTH;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        checkNotNull(outputStream, "outputStream could not be null");
        final Object object = getObject();
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        try {
            mGson.toJson(object, writer);
        } finally {
            writer.close();
        }
    }
}
