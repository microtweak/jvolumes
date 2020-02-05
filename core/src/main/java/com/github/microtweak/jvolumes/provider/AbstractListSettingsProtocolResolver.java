package com.github.microtweak.jvolumes.provider;

import com.github.microtweak.jvolumes.ConfigurableProtocolResolver;
import com.github.microtweak.jvolumes.ProtocolSettings;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListSettingsProtocolResolver<S extends ProtocolSettings> implements ConfigurableProtocolResolver<S> {

    @Getter(AccessLevel.PROTECTED)
    private List<S> settings = new ArrayList<>();

    @Override
    public void addSetting(S setting) {
        settings.add(setting);
    }

}
