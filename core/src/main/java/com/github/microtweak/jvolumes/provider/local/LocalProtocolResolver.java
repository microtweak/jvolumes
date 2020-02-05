package com.github.microtweak.jvolumes.provider.local;

import com.github.microtweak.jvolumes.ResourceLocation;
import com.github.microtweak.jvolumes.provider.AbstractPhysicalDiskProtocolResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalProtocolResolver extends AbstractPhysicalDiskProtocolResolver<LocalProtocolSettings> {

    @Override
    public boolean isSupported(ResourceLocation location) {
        return "local".equals(location.getProtocol());
    }

    @Override
    protected Path getVolumePath(LocalProtocolSettings settings) {
        return Paths.get(settings.getPhysicalDirectory());
    }

}
