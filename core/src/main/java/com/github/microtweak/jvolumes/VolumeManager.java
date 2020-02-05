package com.github.microtweak.jvolumes;

import com.github.microtweak.jvolumes.exception.UnknownProtocolException;
import lombok.Getter;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.TypeVariable;
import java.util.*;

import static com.github.microtweak.jvolumes.ResourceLocation.PROTOCOL_SEPARATOR;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

public final class VolumeManager {

    @Getter
    private static final VolumeManager instance = new VolumeManager();

    private Set<ProtocolResolver> protocols;
    private Map<Class<?>, ConfigurableProtocolResolver> settings;

    private VolumeManager() {
        protocols = new HashSet<>();
        settings = new HashMap<>();

        for (ProtocolResolver resolver : ServiceLoader.load(ProtocolResolver.class)) {
            protocols.add(resolver);

            if (resolver instanceof ConfigurableProtocolResolver) {
                final ConfigurableProtocolResolver configurable = (ConfigurableProtocolResolver) resolver;
                final TypeVariable<?> typeVar = ConfigurableProtocolResolver.class.getTypeParameters()[0];

                settings.put(TypeUtils.getRawType(typeVar, configurable.getClass()), configurable);
            }
        }

        protocols = unmodifiableSet(protocols);
        settings = unmodifiableMap(settings);
    }

    public VolumeManager addSetting(ProtocolSettings setting) {
        Objects.requireNonNull(setting, String.format("You must provide a valid \"%s\" object!", ProtocolSettings.class.getSimpleName()));

        settings.get( setting.getClass() ).addSetting(setting);
        return this;
    }

    public FileResource getItem(String protocol, String path) {
        return getItem(protocol + PROTOCOL_SEPARATOR + path);
    }

    public FileResource getItem(String expression) {
        final ResourceLocation location = new ResourceLocation(expression);
        final String msg = "The protocol \"%s\" is unknown by jVolumes. Check if there is a corresponding module/extension for this protocol and add it as an application dependency!";

        return protocols.stream()
                .filter(r -> r.isSupported(location))
                .map(r -> r.resolve(location))
                .findFirst()
                .orElseThrow(() -> new UnknownProtocolException( String.format(msg, location.getProtocol() + PROTOCOL_SEPARATOR) ) );

    }

}
