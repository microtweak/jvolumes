package com.github.microtweak.jvolumes.google.emulator;

import com.google.cloud.ServiceFactory;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.StorageOptions.DefaultStorageFactory;

public class MinioServiceFactory implements ServiceFactory<Storage, StorageOptions> {

    @Override
    public Storage create(StorageOptions options) {
        return MinioStorageProxy.of( new DefaultStorageFactory().create(options) );
    }

}
