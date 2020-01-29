package com.github.microtweak.storage;

public interface VolumeResolver<S extends VolumeSettings> {

    String getProtocol();

    void addSetting(S setting);

    FileResource resolve(ResourceLocation resourceLocation);

}
