package com.github.microtweak.jvolumes.google;

import com.github.microtweak.jvolumes.ProtocolSettings;
import com.google.auth.Credentials;
import com.google.cloud.ServiceFactory;
import com.google.cloud.spi.ServiceRpcFactory;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.AccessLevel;
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

public class GoogleStorageSettings implements ProtocolSettings {

    private Storage storage;

    private Credentials credentials;
    private String projectId;
    private String host;

    private ServiceFactory<Storage, StorageOptions> serviceFactory;
    private ServiceRpcFactory<StorageOptions> serviceRpcFactory;

    @Getter(AccessLevel.MODULE)
    private List<String> buckets = new ArrayList<>();

    public GoogleStorageSettings fromStorage(Storage storage) {
        this.storage = storage;
        return this;
    }

    public GoogleStorageSettings fromApplicationDefault() throws IOException {
        credentials = getApplicationDefault();
        return this;
    }

    public GoogleStorageSettings fromJson(InputStream inputStream) throws IOException {
        credentials = fromStream(inputStream);
        return this;
    }

    public GoogleStorageSettings fromJson(File file) throws IOException {
        credentials = fromStream( new FileInputStream(file) );
        return this;
    }

    public GoogleStorageSettings fromJson(String file) throws IOException {
        credentials = fromStream( new FileInputStream(file) );
        return this;
    }

    public GoogleStorageSettings projectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public GoogleStorageSettings host(String host) {
        this.host = host;
        return this;
    }

    public GoogleStorageSettings bucket(String bucketName) {
        buckets.add(bucketName);
        return this;
    }

    public GoogleStorageSettings buckets(String... bucketNames) {
        Stream.of(bucketNames).filter(StringUtils::isNotBlank).forEach(buckets::add);
        return this;
    }
    
    protected synchronized Storage create() {
        if (storage == null) {
            final StorageOptions.Builder builder = StorageOptions.newBuilder();

            ofNullable(credentials).ifPresent(builder::setCredentials);

            ofNullable(projectId).filter(StringUtils::isNotBlank).ifPresent(builder::setProjectId);

            ofNullable(host).filter(StringUtils::isNotBlank).ifPresent(builder::setHost);

            ofNullable(serviceFactory).ifPresent(builder::setServiceFactory);

            ofNullable(serviceRpcFactory).ifPresent(builder::setServiceRpcFactory);

            storage = builder.build().getService();
        }

        return storage;
    }

}
