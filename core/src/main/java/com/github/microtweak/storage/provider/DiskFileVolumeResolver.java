package com.github.microtweak.storage.provider;

import com.github.microtweak.storage.FileResource;
import com.github.microtweak.storage.ResourceLocation;
import com.github.microtweak.storage.VolumeResolver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class DiskFileVolumeResolver implements VolumeResolver<NullVolumeSettings> {

    @Override
    public String getProtocol() {
        return "file";
    }

    @Override
    public void addSetting(NullVolumeSettings setting) {
    }

    @Override
    public FileResource resolve(ResourceLocation resourceLocation) {
        final Path resourcePath = Paths.get(resourceLocation.getVolumeName(), resourceLocation.getPath());
        return new PhysicalDiskFileResource( resourcePath, Collections.emptyList() );
    }

}
