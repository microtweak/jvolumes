package com.github.microtweak.jvolumes;

public interface ProtocolResolver {

    boolean isSupported(ResourceLocation location);

    FileResource resolve(ResourceLocation resourceLocation);

}
