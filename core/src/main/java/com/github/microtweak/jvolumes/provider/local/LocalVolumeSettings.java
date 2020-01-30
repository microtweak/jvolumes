package com.github.microtweak.jvolumes.provider.local;

import com.github.microtweak.jvolumes.provider.PhysicalDiskSettings;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.nio.file.attribute.FileAttribute;
import java.util.List;

@Getter
@Builder
public class LocalVolumeSettings implements PhysicalDiskSettings {

    private String name;
    private String physicalDirectory;

    @Singular
    private List<FileAttribute<?>> attributes;

}
