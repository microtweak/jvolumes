package com.github.microtweak.jvolumes.provider;

import com.github.microtweak.jvolumes.ProtocolSettings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PhysicalDiskProtocolSettings<IMPL extends PhysicalDiskProtocolSettings> implements ProtocolSettings {

    @NonNull
    private String name;

    private List<FileAttribute<?>> attributes = new ArrayList<>();

    public IMPL attribute(FileAttribute<?> attribute) {
        attributes.add(attribute);
        return (IMPL) this;
    }

    public IMPL attributes(FileAttribute<?>... attributes) {
        Stream.of(attributes).forEach(this.attributes::add);
        return (IMPL) this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
