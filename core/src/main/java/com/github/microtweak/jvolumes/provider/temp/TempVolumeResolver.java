package com.github.microtweak.jvolumes.provider.temp;

import com.github.microtweak.jvolumes.provider.AbstractPhysicalDiskVolumeResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TempVolumeResolver extends AbstractPhysicalDiskVolumeResolver<TempVolumeSettings> {

    @Override
    public String getProtocol() {
        return "tmp";
    }

    @Override
    protected Path getVolumePath(TempVolumeSettings settings) {
        return Paths.get(System.getProperty("java.io.tmpdir"), settings.getName());
    }

}
