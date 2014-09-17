package pl.cierniewski.friendlygallery.facebookapi.content;

import com.google.api.client.http.HttpContent;

import java.io.IOException;
import java.io.OutputStream;

public class ProgressHttpContent implements HttpContent {
    private final HttpContent mContent;

    private final Object mObserverLock = new Object();
    private ProgressObserver mObserver;

    public static interface ProgressObserver {

        void onProgress(long progress, long length);

        void onSuccess(long length);

        void onError(IOException e);
    }

    public ProgressHttpContent(HttpContent content) {
        mContent = content;
    }

    public ProgressObserver getObserver() {
        synchronized (mObserverLock) {
            return mObserver;
        }
    }

    public void setObserver(ProgressObserver observer) {
        synchronized (mObserverLock) {
            mObserver = observer;
        }
    }

    @Override
    public long getLength() throws IOException {
        return mContent.getLength();
    }

    @Override
    public String getType() {
        return mContent.getType();
    }

    @Override
    public boolean retrySupported() {
        return mContent.retrySupported();
    }

    @Override
    public void writeTo(final OutputStream out) throws IOException {
        if (mObserver == null) {
            mContent.writeTo(out);
            return;
        }
        final long length = getLength();
        callProgress(0L, length);
        try {
            mContent.writeTo(new OutputStream() {
                int position = 0;

                @Override
                public void write(int b) throws IOException {
                    position += 1;
                    out.write(b);

                    callProgress(position, length);
                }

                @Override
                public void write(byte[] b) throws IOException {
                    position += b.length;
                    out.write(b);

                    callProgress(position, length);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    position += off + len;
                    out.write(b, off, len);

                    callProgress(position, length);
                }

                @Override
                public void flush() throws IOException {
                    out.flush();
                }

                @Override
                public void close() throws IOException {
                    position = 0;
                    out.close();
                }
            });
            synchronized (mObserverLock) {
                mObserver.onSuccess(length);
            }
        } catch (IOException e) {
            synchronized (mObserverLock) {
                if (mObserver != null) {
                    mObserver.onError(e);
                }
            }
            throw e;
        }
    }

    protected void callProgress(long progress, long length) {
        synchronized (mObserverLock) {
            if (mObserver != null) {
                mObserver.onProgress(progress, length);
            }
        }
    }
}
