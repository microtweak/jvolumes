package com.github.microtweak.jvolumes.google.emulator;

import com.google.cloud.ServiceRpc;
import com.google.cloud.spi.ServiceRpcFactory;
import com.google.cloud.storage.StorageOptions;

public class MinioServiceRpcFactory implements ServiceRpcFactory<StorageOptions> {

    @Override
    public ServiceRpc create(StorageOptions options) {
        return new MinioStorageRpc(options);
    }

}
