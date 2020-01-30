package com.github.microtweak.jvolumes.provider.local;

import com.github.microtweak.jvolumes.provider.AbstractPhysicalDiskVolumeResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalVolumeResolver extends AbstractPhysicalDiskVolumeResolver<LocalVolumeSettings> {

    @Override
    public String getProtocol() {
        return "local";
    }

    @Override
    protected Path getVolumePath(LocalVolumeSettings settings) {
        return Paths.get(settings.getPhysicalDirectory());
    }

}
