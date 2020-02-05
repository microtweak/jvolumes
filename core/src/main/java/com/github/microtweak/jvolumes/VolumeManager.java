package com.github.microtweak.jvolumes;

import com.github.microtweak.jvolumes.exception.UnknownProtocolException;
import lombok.Getter;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

import static com.github.microtweak.jvolumes.ResourceLocation.PROTOCOL_SEPARATOR;
import static java.util.Collections.unmodifiableMap;

public final class VolumeManager {

    @Getter
    private static final VolumeManager instance = new VolumeManager();

    private Map<String, ProtocolResolver<?>> protocols;
    private Map<Class<?>, ProtocolResolver> settings;

    private VolumeManager() {
        protocols = new HashMap<>();
        settings = new HashMap<>();

        for (ProtocolResolver resolver : ServiceLoader.load(ProtocolResolver.class)) {
            protocols.put(resolver.getProtocol(), resolver);

            TypeVariable<?> typeVar = ProtocolResolver.class.getTypeParameters()[0];
            settings.put(TypeUtils.getRawType(typeVar, resolver.getClass()), resolver);
        }

        protocols = unmodifiableMap(protocols);
        settings = unmodifiableMap(settings);
    }

    public VolumeManager addSetting(ProtocolSettings setting) {
        Objects.requireNonNull(setting, String.format("You must provide a valid \"%s\" object!", ProtocolSettings.class.getSimpleName()));

        settings.get(setting.getClass()).addSetting(setting);
        return this;
    }

    public FileResource getItem(String protocol, String path) {
        return getItem(protocol + PROTOCOL_SEPARATOR + path);
    }

    public FileResource getItem(String expression) {
        final ResourceLocation location = new ResourceLocation(expression);
        final ProtocolResolver<?> resolver = protocols.get( location.getProtocol() );

        if (resolver == null) {
            final String msg = "The protocol \"%s\" is unknown by jVolumes. Check if there is a corresponding module/extension for this protocol and add it as an application dependency!";
            throw new UnknownProtocolException( String.format(msg, location.getProtocol() + PROTOCOL_SEPARATOR) );
        }

        return resolver.resolve(location);
    }

}
