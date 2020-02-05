package com.github.microtweak.jvolumes.provider.temp;

import com.github.microtweak.jvolumes.provider.PhysicalDiskSettings;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class TempVolumeSettings extends PhysicalDiskSettings<TempVolumeSettings> {

    public TempVolumeSettings(@NonNull String name) {
        super(name);
    }

}
