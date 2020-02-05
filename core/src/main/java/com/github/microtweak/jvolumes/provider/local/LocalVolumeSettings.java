package com.github.microtweak.jvolumes.provider.local;

import com.github.microtweak.jvolumes.provider.PhysicalDiskSettings;
import lombok.Getter;

@Getter
public class LocalVolumeSettings extends PhysicalDiskSettings<LocalVolumeSettings> {

    private String physicalDirectory;

    public LocalVolumeSettings(String name, String physicalDirectory) {
        super(name);
        this.physicalDirectory = physicalDirectory;
    }

}
