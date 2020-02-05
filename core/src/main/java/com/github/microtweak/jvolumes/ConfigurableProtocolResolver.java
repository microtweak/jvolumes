package com.github.microtweak.jvolumes;

public interface ConfigurableProtocolResolver<S extends ProtocolSettings> extends ProtocolResolver {

    void addSetting(S setting);

}
