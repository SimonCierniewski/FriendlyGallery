package pl.cierniewski.friendlygallery.facebookapi.content;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static com.google.common.base.Preconditions.checkNotNull;

public class MemoryGsonHttpContent extends GsonHttpContent {

    private byte[] content;

    public MemoryGsonHttpContent(Gson gson) {
        super(gson);
        content = null;
    }

    private byte[] getContent() throws IOException {
        if (content != null) {
            return content;
        }

        final Object object = getObject();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
            try {
                mGson.toJson(object, writer);
            } finally {
                writer.close();
            }
            content = out.toByteArray();
            return content;
        } finally {
            out.close();
        }
    }

    @Override
    public long getLength() throws IOException {
        return getContent().length;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        checkNotNull(outputStream, "outputStream could not be null");
        outputStream.write(getContent());
    }

}
