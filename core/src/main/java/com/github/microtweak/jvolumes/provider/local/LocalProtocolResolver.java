package com.github.microtweak.jvolumes.provider.local;

import com.github.microtweak.jvolumes.provider.AbstractPhysicalDiskProtocolResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalProtocolResolver extends AbstractPhysicalDiskProtocolResolver<LocalProtocolSettings> {

    @Override
    public String getProtocol() {
        return "local";
    }

    @Override
    protected Path getVolumePath(LocalProtocolSettings settings) {
        return Paths.get(settings.getPhysicalDirectory());
    }

}
