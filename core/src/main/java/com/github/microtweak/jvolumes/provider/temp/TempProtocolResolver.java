package com.github.microtweak.jvolumes.provider.temp;

import com.github.microtweak.jvolumes.ResourceLocation;
import com.github.microtweak.jvolumes.provider.AbstractPhysicalDiskProtocolResolver;
import org.apache.commons.io.FileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TempProtocolResolver extends AbstractPhysicalDiskProtocolResolver<TempProtocolSettings> {

    @Override
    public boolean isSupported(ResourceLocation location) {
        return "tmp".equals(location.getProtocol()) || "temp".equals(location.getProtocol());
    }

    @Override
    protected Path getVolumePath(TempProtocolSettings settings) {
        return Paths.get(FileUtils.getTempDirectoryPath(), settings.getName());
    }

}
