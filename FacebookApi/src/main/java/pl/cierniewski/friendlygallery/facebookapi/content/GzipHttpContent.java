package pl.cierniewski.friendlygallery.facebookapi.content;

import com.google.api.client.http.HttpContent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public class GzipHttpContent implements HttpContent {
    private HttpContent mHttpContent;
    private byte[] mContent;

    public GzipHttpContent(HttpContent httpContent) {
        mHttpContent = checkNotNull(httpContent);
    }


    @Override
    public long getLength() throws IOException {
        return getContent().length;
    }

    @Override
    public String getType() {
        return mHttpContent.getType();
    }

    @Override
    public boolean retrySupported() {
        return true;
    }

    private byte[] getContent() throws IOException {
        if (mContent != null) {
            return mContent;
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final OutputStream gzipStream = new GZIPOutputStream(out);
            try {
                mHttpContent.writeTo(gzipStream);
            } finally {
                gzipStream.close();
            }
            mContent = out.toByteArray();
            return mContent;
        } finally {
            out.close();
        }
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(getContent());
    }
}
