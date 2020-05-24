package com.github.microtweak.jvolumes.google.emulator;

import io.minio.errors.MinioException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@FunctionalInterface
interface MinioAction<R> {

    R execute() throws MinioException, InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, IOException;

    static <R> R doAction(MinioAction<R> action) {
        try {
            return action.execute();
        } catch (InvalidKeyException | IllegalArgumentException | NoSuchAlgorithmException | MinioException | IOException e) {
            return ExceptionUtils.rethrow(e);
        }
    }
}
