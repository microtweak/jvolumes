package com.github.microtweak.jvolumes.google.emulator;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.SignUrlOption;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
class MinioStorageProxy implements InvocationHandler {

    private Storage impl;

    static Storage of(Storage impl) {
        final InvocationHandler handler = new MinioStorageProxy(impl);
        return (Storage) Proxy.newProxyInstance(Storage.class.getClassLoader(), new Class[] { Storage.class }, handler);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("signUrl".equals(method.getName())) {
            final BlobInfo blobInfo = (BlobInfo) args[0];
            final long duration = (long) args[1];
            final TimeUnit unit = (TimeUnit) args[2];
            final SignUrlOption[] options = (SignUrlOption[]) args[3];

            return getRpc().signUrl(blobInfo, duration, unit, options);
        }

        try {
            final Object result = method.invoke(impl, args);

            if (result instanceof Bucket || result instanceof Blob) {
                FieldUtils.writeField(result, "storage", proxy, true);
            }

            return result;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private MinioStorageRpc getRpc() {
        return (MinioStorageRpc) impl.getOptions().getRpc();
    }

}