package com.github.microtweak.jvolumes.provider.temp;

import com.github.microtweak.jvolumes.FileResource;
import com.github.microtweak.jvolumes.ResourceLocation;
import com.github.microtweak.jvolumes.exception.UnknownVolumeException;
import com.github.microtweak.jvolumes.provider.AbstractListSettingsVolumeResolver;
import com.github.microtweak.jvolumes.provider.PhysicalDiskFileResource;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TempVolumeResolver extends AbstractListSettingsVolumeResolver<TempVolumeSettings> {

    @Override
    public String getProtocol() {
        return "tmp";
    }

    @Override
    public FileResource resolve(ResourceLocation resourceLocation) {
        final TempVolumeSettings settings = getSettings().stream()
                .filter(s -> s.getName().equals(resourceLocation.getVolumeName()))
                .findFirst()
                .orElseThrow(() -> new UnknownVolumeException( String.format("No volume named \"%s\" defined previously", resourceLocation.getVolumeName()) ));

        final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), settings.getName());
        final Path subPath = Paths.get( resourceLocation.getPath() );

        return new PhysicalDiskFileResource( tempDir.resolve(subPath), settings.getAttributes() ) {};
    }

}
