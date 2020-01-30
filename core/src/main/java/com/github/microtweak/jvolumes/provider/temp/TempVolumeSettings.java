package com.github.microtweak.jvolumes.provider.temp;

import com.github.microtweak.jvolumes.provider.PhysicalDiskSettings;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.nio.file.attribute.FileAttribute;
import java.util.List;

@Getter
@Builder
public class TempVolumeSettings implements PhysicalDiskSettings {

    private String name;

    @Singular
    private List<FileAttribute<?>> attributes;

}
