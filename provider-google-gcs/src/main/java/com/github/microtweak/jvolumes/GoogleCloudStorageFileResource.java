package com.github.microtweak.jvolumes;

import com.github.microtweak.jvolumes.io.CallbackOutputStream;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;

import java.io.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static com.github.microtweak.jvolumes.ResourceLocation.FILE_EXTENSION_SEPARATOR;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

public class GoogleCloudStorageFileResource implements FileResource {

    private ResourceLocation location;

    private Bucket bucket;
    private Blob blob;

    GoogleCloudStorageFileResource(ResourceLocation location, Storage storage) {
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

    private void checkIfBlobExists() throws FileNotFoundException {
        if (blob == null) {
            final String msg = "The file \"%s\" does not exist in the bucket \"%s\" Google Cloud Storage!";
            throw new FileNotFoundException( String.format(msg, location.getPath(), getBucket()) );
        }
    }

    @Override
    public URL getUrl() throws IOException {
        checkIfBlobExists();
        return new URL(blob.getSelfLink());
    }

    public URL getSignedUrl(long duration, TimeUnit unit, Storage.SignUrlOption... options) throws IOException {
        checkIfBlobExists();
        return blob.signUrl(10, TimeUnit.MINUTES);
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

    private void copyToAnotherBucket(GoogleCloudStorageFileResource dest) {
        ResourceLocation location = dest.location;
        blob.copyTo(location.getVolumeName(), location.getPath());
    }

    @Override
    public FileResource copyTo(FileResource dest) throws IOException {
        checkIfBlobExists();

        if (dest instanceof GoogleCloudStorageFileResource) {
            copyToAnotherBucket( (GoogleCloudStorageFileResource) dest );
            return dest;
        }

        return FileResource.super.copyTo(dest);
    }

    @Override
    public FileResource moveTo(FileResource dest) throws IOException {
        checkIfBlobExists();

        if (dest instanceof GoogleCloudStorageFileResource) {
            copyToAnotherBucket( (GoogleCloudStorageFileResource) dest );

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
