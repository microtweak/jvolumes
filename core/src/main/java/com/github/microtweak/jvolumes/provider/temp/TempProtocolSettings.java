package com.github.microtweak.jvolumes.provider.temp;

import com.github.microtweak.jvolumes.provider.PhysicalDiskProtocolSettings;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class TempProtocolSettings extends PhysicalDiskProtocolSettings<TempProtocolSettings> {

    public TempProtocolSettings(@NonNull String name) {
        super(name);
    }

}
