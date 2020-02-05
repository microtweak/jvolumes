package com.github.microtweak.jvolumes.provider;

import com.github.microtweak.jvolumes.FileResource;
import com.github.microtweak.jvolumes.ResourceLocation;
import com.github.microtweak.jvolumes.ProtocolResolver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class DiskFileProtocolResolver implements ProtocolResolver<NullProtocolSettings> {

    @Override
    public String getProtocol() {
        return "file";
    }

    @Override
    public void addSetting(NullProtocolSettings setting) {
    }

    @Override
    public FileResource resolve(ResourceLocation resourceLocation) {
        final Path resourcePath = Paths.get(resourceLocation.getVolumeName(), resourceLocation.getPath());
        return new PhysicalDiskFileResource( resourcePath, Collections.emptyList() );
    }

}
