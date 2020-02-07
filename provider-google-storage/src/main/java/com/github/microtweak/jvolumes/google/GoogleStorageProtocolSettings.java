package com.github.microtweak.jvolumes.google;

import com.github.microtweak.jvolumes.ProtocolSettings;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.Getter;
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
import static java.util.Optional.ofNullable;

@Getter
public class GoogleStorageProtocolSettings implements ProtocolSettings {

    private GoogleCredentials credentials;
    private Storage storage;

    private String projectId;
    private List<String> buckets = new ArrayList<>();

    private GoogleStorageProtocolSettings(GoogleCredentials credentials) {
        this.credentials = credentials;
    }

    private GoogleStorageProtocolSettings(Storage storage) {
        this.storage = storage;
    }

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

    public static GoogleStorageProtocolSettings fromStorage(Storage storage) {
        return new GoogleStorageProtocolSettings(storage);
    }

    public GoogleStorageProtocolSettings projectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public GoogleStorageProtocolSettings bucket(String bucketName) {
        buckets.add(bucketName);
        return this;
    }

    public GoogleStorageProtocolSettings buckets(String... bucketNames) {
        Stream.of(bucketNames).filter(StringUtils::isNotBlank).forEach(buckets::add);
        return this;
    }

    public synchronized Storage getStorage() {
        if (storage == null) {
            StorageOptions.Builder builder = StorageOptions.newBuilder();

            ofNullable(credentials).ifPresent(builder::setCredentials);

            ofNullable(projectId).ifPresent(builder::setProjectId);

            storage = builder.build().getService();
        }

        return storage;
    }
}
