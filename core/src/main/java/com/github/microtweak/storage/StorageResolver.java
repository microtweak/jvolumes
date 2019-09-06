package com.github.microtweak.storage;

public interface StorageResolver {

    boolean canResolve(String protocol);

    StorageItem resolve(String path);

}
