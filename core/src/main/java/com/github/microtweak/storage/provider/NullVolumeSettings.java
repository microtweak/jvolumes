package com.github.microtweak.storage.provider;

import com.github.microtweak.storage.VolumeSettings;
import lombok.Getter;

public final class NullVolumeSettings implements VolumeSettings {

    @Getter
    private static final VolumeSettings instance = new NullVolumeSettings();

}
