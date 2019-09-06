package com.github.microtweak.storage;

import java.io.InputStream;
import java.io.OutputStream;

public interface StorageItem {

    String getName();

    String getExtension();

    long length();

    InputStream getInputStream();

    OutputStream getOutputStream();

    void copyTo(StorageItem dest);

    void moveTo(StorageItem dest);

    void delete();

}
