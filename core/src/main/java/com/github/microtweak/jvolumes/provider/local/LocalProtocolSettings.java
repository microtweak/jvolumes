package com.github.microtweak.jvolumes.provider.local;

import com.github.microtweak.jvolumes.provider.PhysicalDiskProtocolSettings;
import lombok.Getter;

@Getter
public class LocalProtocolSettings extends PhysicalDiskProtocolSettings<LocalProtocolSettings> {

    private String physicalDirectory;

    public LocalProtocolSettings(String name, String physicalDirectory) {
        super(name);
        this.physicalDirectory = physicalDirectory;
    }

}
