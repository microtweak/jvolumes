package com.github.microtweak.jvolumes.provider;

import com.github.microtweak.jvolumes.VolumeSettings;

import java.nio.file.attribute.FileAttribute;
import java.util.List;

public interface PhysicalDiskSettings extends VolumeSettings {

    String getName();

    List<FileAttribute<?>> getAttributes();

}
