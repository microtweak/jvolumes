package com.github.microtweak.storage;


import com.github.microtweak.storage.exception.UnknownDeviceProtocolException;
import lombok.Getter;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

import static java.util.Collections.unmodifiableMap;

public final class VolumeManager {

    private static final String PROTOCOL_SEPARATOR = "://";

    @Getter
    private static final VolumeManager instance = new VolumeManager();

    private Map<String, VolumeResolver<?>> protocols;
    private Map<Class<?>, VolumeResolver> settings;

    private VolumeManager() {
        protocols = new HashMap<>();
        settings = new HashMap<>();

        for (VolumeResolver resolver : ServiceLoader.load(VolumeResolver.class)) {
            protocols.put(resolver.getProtocol(), resolver);

            TypeVariable<?> typeVar = VolumeResolver.class.getTypeParameters()[0];
            settings.put(TypeUtils.getRawType(typeVar, resolver.getClass()), resolver);
        }

        protocols = unmodifiableMap(protocols);
        settings = unmodifiableMap(settings);
    }

    public VolumeManager newDevice(VolumeSettings setting) {
        Objects.requireNonNull(setting, String.format("You must provide a valid \"%s\" object!", VolumeSettings.class.getSimpleName()));

        settings.get(setting.getClass()).addSetting(setting);
        return this;
    }

    public FileResource getItem(String protocol, String path) {
        return getItem(protocol + PROTOCOL_SEPARATOR + path);
    }

    public FileResource getItem(String expression) {
        final ResourceLocation location = new ResourceLocation(expression);
        final VolumeResolver<?> resolver = protocols.get( location.getProtocol() );

        if (resolver == null) {
            final String msg = "The protocol \"%s\" is unknown by jVolumes. Check if there is a corresponding module/extension for this protocol and add it as an application dependency!";
            throw new UnknownDeviceProtocolException( String.format(msg, location.getProtocol() + PROTOCOL_SEPARATOR) );
        }

        return resolver.resolve(location);
    }

}
