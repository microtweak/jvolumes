package com.github.microtweak.jvolumes;

import com.github.microtweak.jvolumes.exception.UnknownVolumeException;
import com.github.microtweak.jvolumes.provider.AbstractListSettingsVolumeResolver;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GoogleCloudStorageVolumeResolver extends AbstractListSettingsVolumeResolver<GoogleCloudStorageSettings> {

    private final Map<String, Storage> volumeNameStorages = new ConcurrentHashMap<>();

    @Override
    public String getProtocol() {
        return "gs";
    }

    @Override
    public FileResource resolve(ResourceLocation resourceLocation) {
        final GoogleCloudStorageSettings settings = findSettings( resourceLocation.getVolumeName() );
        final Storage storage = volumeNameStorages.computeIfAbsent( resourceLocation.getVolumeName(), (k) -> newStorage(settings));

        return new GoogleCloudStorageFileResource(resourceLocation, storage);
    }

    private GoogleCloudStorageSettings findSettings(String volumeName) {
        switch (getSettings().size()) {
            case 0:
                throw new UnknownVolumeException("No credentials available to access Google Cloud Storage buckets!");

            case 1:
                return getSettings().get(0);

            default:
                return getSettings().stream()
                        .filter(s -> s.getBuckets().contains(volumeName) )
                        .findFirst()
                        .orElseThrow(() -> new UnknownVolumeException(String.format("No Google Cloud credentials for bucket \"%s\"!", volumeName)));
        }
    }

    private Storage newStorage(GoogleCloudStorageSettings settings) {
        return StorageOptions.newBuilder()
                .setCredentials( settings.getCredentials() )
                .build()
                .getService();
    }

}
