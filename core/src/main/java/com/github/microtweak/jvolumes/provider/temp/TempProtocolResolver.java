package com.github.microtweak.jvolumes.provider.temp;

import com.github.microtweak.jvolumes.provider.AbstractPhysicalDiskProtocolResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TempProtocolResolver extends AbstractPhysicalDiskProtocolResolver<TempProtocolSettings> {

    @Override
    public String getProtocol() {
        return "tmp";
    }

    @Override
    protected Path getVolumePath(TempProtocolSettings settings) {
        return Paths.get(System.getProperty("java.io.tmpdir"), settings.getName());
    }

}
