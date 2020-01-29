package com.github.microtweak.storage;

import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.substringAfter;

@Getter
public final class ResourceLocation {

    private String protocol;

    private String volumeName;

    private String path;

    private Map<String, String> parameters;

    ResourceLocation(String text) {
        URI uri = null;

        try {
            uri = new URI(text);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        protocol = uri.getScheme();

        volumeName = uri.getAuthority();

        path = substringAfter(uri.getPath(), "/");

        parameters = getParameterMap(uri.getQuery());
    }

    private Map<String, String> getParameterMap(String query) {
        if (query == null) {
            return Collections.emptyMap();
        }

        Map<String, String> params = Stream.of(query.split("&"))
            .map(param -> param.split("="))
            .peek(parts -> parts[1] = decode(parts[1]))
            .map(parts -> new AbstractMap.SimpleEntry<>(parts[0], parts[1]))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        return Collections.unmodifiableMap(params);
    }

    private String decode(String text) {
        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

}
