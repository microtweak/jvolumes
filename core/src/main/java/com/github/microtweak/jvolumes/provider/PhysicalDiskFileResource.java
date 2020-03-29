package com.github.microtweak.jvolumes.provider;

import com.github.microtweak.jvolumes.FileResource;
import com.github.microtweak.jvolumes.ResourceLocation;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;

import static com.github.microtweak.jvolumes.ResourceLocation.FILE_EXTENSION_SEPARATOR;
import static com.github.microtweak.jvolumes.exception.FileResourceNotFoundException.fireIfNotFoundException;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

public class PhysicalDiskFileResource implements FileResource {

    @Getter
    private ResourceLocation location;

    private Path resourcePath;
    private FileAttribute<?>[] attributes;

    public PhysicalDiskFileResource(ResourceLocation location, Path resourcePath, List<FileAttribute<?>> attributes) {
        this.location = location;
        this.resourcePath = resourcePath;
        this.attributes = attributes.toArray(new FileAttribute[ attributes.size() ]);
    }

    private String getFileName() {
        return resourcePath.getFileName().toString();
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
        try {
            return Files.size(resourcePath);
        } catch (IOException e) {
            return -1L;
        }
    }

    @Override
    public URL getUrl() {
        try {
            return resourcePath.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            return Files.newInputStream(resourcePath);
        } catch (IOException e) {
            fireIfNotFoundException(e, getFileName());
            throw e;
        }
    }

    private void tryCreateDirectories(FileAttribute<?>... attributes) throws IOException {
        final Path dir = resourcePath.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir, attributes);
        }
    }

    private void tryCreateFile(FileAttribute<?>... attributes) throws IOException {
        if (!Files.exists(resourcePath)) {
            Files.createFile(resourcePath, attributes);
        }
    }

    public OutputStream getOutputStream(FileAttribute<?>... attributes) throws IOException {
        tryCreateDirectories(attributes);

        tryCreateFile(attributes);

        return Files.newOutputStream(resourcePath);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return getOutputStream(attributes);
    }

    private PhysicalDiskFileResource copyTo(PhysicalDiskFileResource dest) throws IOException {
        dest.tryCreateDirectories(attributes);

        try {
            Files.copy(resourcePath, dest.resourcePath);
        } catch (IOException e) {
            fireIfNotFoundException(e, getFileName());
            throw e;
        }

        return dest;
    }

    @Override
    public FileResource copyTo(FileResource dest) throws IOException {
        if (dest instanceof PhysicalDiskFileResource) {
            return copyTo( (PhysicalDiskFileResource) dest );
        }
        return FileResource.super.copyTo(dest);
    }

    @Override
    public FileResource moveTo(FileResource dest) throws IOException {
        if (dest instanceof PhysicalDiskFileResource) {
            PhysicalDiskFileResource diskResourceDest = copyTo((PhysicalDiskFileResource) dest);

            delete();

            return diskResourceDest;
        }
        return FileResource.super.moveTo(dest);
    }

    @Override
    public void delete() throws IOException {
        try {
            Files.delete(resourcePath);
        } catch (IOException e) {
            fireIfNotFoundException(e, getFileName());
            throw e;
        }
    }

}
