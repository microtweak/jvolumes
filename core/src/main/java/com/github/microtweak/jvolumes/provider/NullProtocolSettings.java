package com.github.microtweak.jvolumes.provider;

import com.github.microtweak.jvolumes.ProtocolSettings;
import lombok.Getter;

public final class NullProtocolSettings implements ProtocolSettings {

    @Getter
    private static final ProtocolSettings instance = new NullProtocolSettings();

}
