package com.github.microtweak.jvolumes.io;

import lombok.Setter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

public class CallbackOutputStream<OS extends OutputStream> extends FilterOutputStream {

    @Setter
    private Consumer<OS> onCloseCallback;

    public CallbackOutputStream(OS out) {
        super(out);
    }

    private void fireCallback(Consumer<OS> callback) {
        if (callback != null) {
            callback.accept((OS) out);
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        fireCallback(onCloseCallback);
    }

}
