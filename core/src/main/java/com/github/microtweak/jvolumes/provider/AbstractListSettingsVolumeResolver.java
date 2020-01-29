package com.github.microtweak.jvolumes.provider;

import com.github.microtweak.jvolumes.VolumeResolver;
import com.github.microtweak.jvolumes.VolumeSettings;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListSettingsVolumeResolver<S extends VolumeSettings> implements VolumeResolver<S> {

    @Getter(AccessLevel.PROTECTED)
    private List<S> settings = new ArrayList<>();

    @Override
    public void addSetting(S setting) {
        settings.add(setting);
    }

}
