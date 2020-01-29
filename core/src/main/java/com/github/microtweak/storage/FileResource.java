package com.github.microtweak.storage;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileResource {

    static FileResource of(String protocol, String path) {
        return VolumeManager.getInstance().getItem(protocol, path);
    }

    static FileResource of(String expression) {
        return VolumeManager.getInstance().getItem(expression);
    }

    String getName();

    String getExtension();

    long length() throws IOException;

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;

    default FileResource copyTo(String dest) throws IOException {
        return copyTo( VolumeManager.getInstance().getItem(dest) );
    }

    default FileResource copyTo(FileResource dest) throws IOException {
        IOUtils.copy(getInputStream(), dest.getOutputStream());
        return dest;
    }

    default FileResource moveTo(String dest) throws IOException {
        return moveTo( VolumeManager.getInstance().getItem(dest) );
    }

    default FileResource moveTo(FileResource dest) throws IOException {
        copyTo(dest);
        delete();
        return dest;
    }

    void delete() throws IOException;

}
