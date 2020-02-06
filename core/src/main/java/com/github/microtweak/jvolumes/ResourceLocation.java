package com.github.microtweak.jvolumes;

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
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.substringAfter;

public final class ResourceLocation {

    public static final String PROTOCOL_SEPARATOR = "://";
    public static final String FILE_EXTENSION_SEPARATOR = ".";

    private URI uri;

    @Getter
    private Map<String, String> parameters;

    ResourceLocation(String text) {
        uri = parseUri(text);
        parameters = getParameterMap(uri.getQuery());
    }

    public String getProtocol() {
        return uri.getScheme();
    }

    public String getVolumeName() {
        return defaultIfBlank(uri.getAuthority(), "");
    }

    public String getPath() {
        return substringAfter(uri.getPath(), "/");
    }

    @Override
    public String toString() {
        return String.join("", getProtocol(), PROTOCOL_SEPARATOR, getVolumeName(), "/", getPath());
    }

    private URI parseUri(String text) {
        try {
            return new URI(text);
        } catch (URISyntaxException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    private Map<String, String> getParameterMap(String query) {
        if (query == null) {
            return Collections.emptyMap();
        }

        Map<String, String> params = Stream.of(query.split("&"))
            .map(param -> param.split("="))
            .peek(parts -> parts[1] = decodeUrl(parts[1]))
            .map(parts -> new AbstractMap.SimpleEntry<>(parts[0], parts[1]))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        return Collections.unmodifiableMap(params);
    }

    private String decodeUrl(String text) {
        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

}
