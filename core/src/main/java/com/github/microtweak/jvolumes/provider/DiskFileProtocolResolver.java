package com.github.microtweak.jvolumes.provider;

import com.github.microtweak.jvolumes.FileResource;
import com.github.microtweak.jvolumes.ProtocolResolver;
import com.github.microtweak.jvolumes.ResourceLocation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class DiskFileProtocolResolver implements ProtocolResolver {

    @Override
    public boolean isSupported(ResourceLocation location) {
        return "file".equals(location.getProtocol());
    }

    @Override
    public FileResource resolve(ResourceLocation resourceLocation) {
        final Path resourcePath = Paths.get(resourceLocation.getVolumeName(), resourceLocation.getPath());
        return new PhysicalDiskFileResource( resourcePath, Collections.emptyList() );
    }

}
