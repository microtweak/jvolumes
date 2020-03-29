package com.github.microtweak.jvolumes.exception;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class FileResourceNotFoundException extends JVolumeException {

    public FileResourceNotFoundException(String message) {
        super(message);
    }

    public FileResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void fireIfNotFoundException(IOException cause, String fileName) {
        if (cause instanceof FileNotFoundException || cause instanceof NoSuchFileException) {
            final String msg = String.format("The file \"%s\" does not exist on the file system!", fileName);
            throw new FileResourceNotFoundException(msg, cause);
        }
    }

}
