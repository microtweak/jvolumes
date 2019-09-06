package com.github.microtweak.storage;


import org.apache.commons.lang3.StringUtils;

import java.util.ServiceLoader;

public final class StorageManager {

    private static final String PROTOCOL_AND_PATH_SEPARATOR = "://";

    public static StorageItem getItem(String location) {
        String[] parts = StringUtils.split(location, PROTOCOL_AND_PATH_SEPARATOR);
        return getItem(parts[0], parts[1]);
    }

    public static StorageItem getItem(String protocol, String path) {
        StorageResolver sr = findResolver(protocol);
        return sr.resolve(path);
    }

    private static StorageResolver findResolver(String protocol) {
        for (StorageResolver sr : ServiceLoader.load(StorageResolver.class)) {
            if (sr.canResolve(protocol)) {
                return sr;
            }
        }
        return null;
    }

}
