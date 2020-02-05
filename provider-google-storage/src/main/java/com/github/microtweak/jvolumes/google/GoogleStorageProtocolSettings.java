package com.github.microtweak.jvolumes.google;

import com.github.microtweak.jvolumes.ProtocolSettings;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.google.auth.oauth2.GoogleCredentials.fromStream;
import static com.google.auth.oauth2.GoogleCredentials.getApplicationDefault;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GoogleStorageProtocolSettings implements ProtocolSettings {

    @NonNull
    private GoogleCredentials credentials;
    private List<String> buckets = new ArrayList<>();

    public static GoogleStorageProtocolSettings fromApplicationDefault() throws IOException {
        return new GoogleStorageProtocolSettings( getApplicationDefault() );
    }

    public static GoogleStorageProtocolSettings fromJson(InputStream inputStream) throws IOException {
        return new GoogleStorageProtocolSettings( fromStream(inputStream) );
    }

    public static GoogleStorageProtocolSettings fromJson(File file) throws IOException {
        return fromJson( new FileInputStream(file) );
    }

    public static GoogleStorageProtocolSettings fromJson(String file) throws IOException {
        return fromJson( new FileInputStream(file) );
    }

    public GoogleStorageProtocolSettings bucket(String bucketName) {
        buckets.add(bucketName);
        return this;
    }

    public GoogleStorageProtocolSettings buckets(String... bucketNames) {
        Stream.of(bucketNames).filter(StringUtils::isNotBlank).forEach(buckets::add);
        return this;
    }

}
