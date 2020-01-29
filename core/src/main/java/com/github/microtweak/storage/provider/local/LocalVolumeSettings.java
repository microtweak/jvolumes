package com.github.microtweak.storage.provider.local;

import com.github.microtweak.storage.VolumeSettings;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.nio.file.attribute.FileAttribute;
import java.util.List;

@Getter
@Builder
public class LocalVolumeSettings implements VolumeSettings {

    private String name;

    private String physicalDirectory;

    @Singular
    private List<FileAttribute<?>> attributes;

}
