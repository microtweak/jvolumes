package com.github.microtweak.jvolumes.provider;

import com.github.microtweak.jvolumes.VolumeSettings;
import lombok.Getter;

public final class NullVolumeSettings implements VolumeSettings {

    @Getter
    private static final VolumeSettings instance = new NullVolumeSettings();

}
