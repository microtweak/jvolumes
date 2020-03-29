package com.github.microtweak.jvolumes.google;

import com.github.microtweak.jvolumes.FileResource;
import com.github.microtweak.jvolumes.ResourceLocation;
import com.github.microtweak.jvolumes.exception.FileResourceNotFoundException;
import com.github.microtweak.jvolumes.io.CallbackOutputStream;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import lombok.Getter;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.github.microtweak.jvolumes.ResourceLocation.FILE_EXTENSION_SEPARATOR;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

public class GoogleStorageFileResource implements FileResource {

    @Getter
    private ResourceLocation location;

    private Bucket bucket;
    private Blob blob;

    GoogleStorageFileResource(ResourceLocation location, Storage storage) {
        this.location = location;
        final String bucketName = location.getVolumeName();

        bucket = ofNullable( storage.get( bucketName ) )
                .orElseGet(() -> storage.create( BucketInfo.of( bucketName ) ));

        blob = bucket.get( location.getPath() );
    }

    public String getBucket() {
        return bucket.getName();
    }

    public String getFileName() {
        return ofNullable(blob).map(Blob::getName).orElse(location.getPath());
    }

    @Override
    public String getName() {
        return substringBeforeLast(getFileName(), FILE_EXTENSION_SEPARATOR);
    }

    @Override
    public String getExtension() {
        return substringAfterLast(getFileName(), FILE_EXTENSION_SEPARATOR);
    }

    @Override
    public long length() {
        return ofNullable(blob).map(Blob::getSize).orElse( -1L);
    }

    private void checkIfBlobExists() {
        if (blob == null) {
            final String msg = "The file \"%s\" does not exist in the bucket \"%s\" Google Cloud Storage!";
            throw new FileResourceNotFoundException( String.format(msg, location.getPath(), getBucket()) );
        }
    }

    @Override
    public URL getUrl() {
        checkIfBlobExists();
        try {
            return new URL(blob.getSelfLink());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public URL getSignedUrl(long duration, ChronoUnit chronoUnit) {
        checkIfBlobExists();

        final TimeUnit timeUnit;

        switch (Objects.requireNonNull(chronoUnit, "chronoUnit")) {
            case NANOS:
                timeUnit = TimeUnit.NANOSECONDS;
                break;

            case MICROS:
                timeUnit = TimeUnit.MICROSECONDS;
                break;

            case MILLIS:
                timeUnit = TimeUnit.MILLISECONDS;
                break;

            case SECONDS:
                timeUnit = TimeUnit.SECONDS;
                break;

            case MINUTES:
                timeUnit = TimeUnit.MINUTES;
                break;

            case HOURS:
                timeUnit = TimeUnit.HOURS;
                break;

            case DAYS:
                timeUnit = TimeUnit.DAYS;
                break;

            default:
                throw new IllegalArgumentException("No TimeUnit equivalent for " + chronoUnit);
        }

        return blob.signUrl(duration, timeUnit);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        checkIfBlobExists();

        byte[] content = blob.getContent();

        return new ByteArrayInputStream( content );
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        final CallbackOutputStream<ByteArrayOutputStream> out = new CallbackOutputStream<>(new ByteArrayOutputStream());
        out.setOnCloseCallback(os -> blob = bucket.create(location.getPath(), os.toByteArray()) );

        return out;
    }

    private void copyToAnotherBucket(GoogleStorageFileResource dest) {
        ResourceLocation location = dest.location;
        blob.copyTo(location.getVolumeName(), location.getPath());
    }

    @Override
    public FileResource copyTo(FileResource dest) throws IOException {
        checkIfBlobExists();

        if (dest instanceof GoogleStorageFileResource) {
            copyToAnotherBucket( (GoogleStorageFileResource) dest );
            return dest;
        }

        return FileResource.super.copyTo(dest);
    }

    @Override
    public FileResource moveTo(FileResource dest) throws IOException {
        checkIfBlobExists();

        if (dest instanceof GoogleStorageFileResource) {
            copyToAnotherBucket( (GoogleStorageFileResource) dest );

            delete();

            return dest;
        }

        return FileResource.super.moveTo(dest);
    }

    @Override
    public void delete() throws IOException {
        checkIfBlobExists();

        blob.delete();
        blob = null;
    }

}
