package com.github.microtweak.jvolumes.google;

import com.github.microtweak.jvolumes.FileResource;
import com.github.microtweak.jvolumes.ResourceLocation;
import com.github.microtweak.jvolumes.exception.UnknownVolumeException;
import com.github.microtweak.jvolumes.provider.AbstractListSettingsProtocolResolver;

public class GoogleStorageProtocolResolver extends AbstractListSettingsProtocolResolver<GoogleStorageProtocolSettings> {

    @Override
    public boolean isSupported(ResourceLocation location) {
        return "gs".equals(location.getProtocol());
    }

    @Override
    public FileResource resolve(ResourceLocation resourceLocation) {
        final GoogleStorageProtocolSettings settings = findSettings( resourceLocation.getVolumeName() );
        return new GoogleStorageFileResource(resourceLocation, settings.getStorage());
    }

    private GoogleStorageProtocolSettings findSettings(String volumeName) {
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

}
