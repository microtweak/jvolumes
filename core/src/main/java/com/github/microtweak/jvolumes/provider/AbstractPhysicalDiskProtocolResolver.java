package com.github.microtweak.jvolumes.provider;

import com.github.microtweak.jvolumes.FileResource;
import com.github.microtweak.jvolumes.ResourceLocation;
import com.github.microtweak.jvolumes.exception.UnknownVolumeException;

import java.nio.file.Path;

public abstract class AbstractPhysicalDiskProtocolResolver<S extends PhysicalDiskProtocolSettings> extends AbstractListSettingsProtocolResolver<S> {

    @Override
    public FileResource resolve(ResourceLocation resourceLocation) {
        final S settings = getSettings().stream()
                .filter(s -> s.getName().equals(resourceLocation.getVolumeName()))
                .findFirst()
                .orElseThrow(() -> new UnknownVolumeException( String.format("No volume named \"%s\" defined previously", resourceLocation.getVolumeName()) ));

        final Path resourcePath = getVolumePath( settings ).resolve( resourceLocation.getPath() );

        return new PhysicalDiskFileResource( resourcePath, settings.getAttributes() ) {};
    }

    protected abstract Path getVolumePath(S settings);

}
