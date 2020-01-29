package com.github.microtweak.jvolumes.provider.local;

import com.github.microtweak.jvolumes.exception.UnknownVolumeException;
import com.github.microtweak.jvolumes.FileResource;
import com.github.microtweak.jvolumes.ResourceLocation;
import com.github.microtweak.jvolumes.provider.AbstractListSettingsVolumeResolver;
import com.github.microtweak.jvolumes.provider.PhysicalDiskFileResource;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalVolumeResolver extends AbstractListSettingsVolumeResolver<LocalVolumeSettings> {

    @Override
    public String getProtocol() {
        return "local";
    }

    @Override
    public FileResource resolve(ResourceLocation resourceLocation) {
        final LocalVolumeSettings settings =  getSettings().stream()
                .filter(s -> s.getName().equals(resourceLocation.getVolumeName()))
                .findFirst()
                .orElseThrow(() -> new UnknownVolumeException( String.format("No volume named \"%s\" defined previously", resourceLocation.getVolumeName()) ));

        final Path dir = Paths.get(settings.getPhysicalDirectory());
        final Path subPath = Paths.get( resourceLocation.getPath() );

        return new PhysicalDiskFileResource( dir.resolve(subPath), settings.getAttributes() ) {};
    }

}
