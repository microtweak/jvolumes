package com.github.microtweak.storage.provider;

import com.github.microtweak.storage.FileResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

public class PhysicalDiskFileResource implements FileResource {

    private Path resourcePath;
    private FileAttribute<?>[] attributes;

    public PhysicalDiskFileResource(Path resourcePath, List<FileAttribute<?>> attributes) {
        this.resourcePath = resourcePath;
        this.attributes = attributes.toArray(new FileAttribute[ attributes.size() ]);
    }

    private String getFileName() {
        return resourcePath.getFileName().toString();
    }

    @Override
    public String getName() {
        return substringBeforeLast(getFileName(), ".");
    }

    @Override
    public String getExtension() {
        return substringAfterLast(getFileName(), ".");
    }

    @Override
    public long length() throws IOException {
        return Files.size(resourcePath);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(resourcePath);
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

        Files.copy(resourcePath, dest.resourcePath);

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
        Files.delete(resourcePath);
    }

}
