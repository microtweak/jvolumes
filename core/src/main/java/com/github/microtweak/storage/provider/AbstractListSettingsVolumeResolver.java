package com.github.microtweak.storage.provider;

import com.github.microtweak.storage.VolumeResolver;
import com.github.microtweak.storage.VolumeSettings;
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
