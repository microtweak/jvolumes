package com.github.microtweak.jvolumes;

public interface ProtocolResolver<S extends ProtocolSettings> {

    String getProtocol();

    void addSetting(S setting);

    FileResource resolve(ResourceLocation resourceLocation);

}
