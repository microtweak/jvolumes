package com.github.microtweak.jvolumes;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public interface FileResource {

    static FileResource of(String protocol, String path) {
        return JVolumes.getInstance().getItem(protocol, path);
    }

    static FileResource of(String expression) {
        return JVolumes.getInstance().getItem(expression);
    }

    ResourceLocation getLocation();

    String getName();

    String getExtension();

    long length();

    URL getUrl();

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;

    default FileResource copyTo(String dest) throws IOException {
        return copyTo( of(dest) );
    }

    default FileResource copyTo(FileResource dest) throws IOException {
        try (OutputStream out = dest.getOutputStream()) {
            IOUtils.copy(getInputStream(), out);
        }
        return dest;
    }

    default FileResource moveTo(String dest) throws IOException {
        return moveTo( of(dest) );
    }

    default FileResource moveTo(FileResource dest) throws IOException {
        copyTo(dest);
        delete();
        return dest;
    }

    void delete() throws IOException;

}
