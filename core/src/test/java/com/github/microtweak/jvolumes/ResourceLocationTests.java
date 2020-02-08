package com.github.microtweak.jvolumes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceLocationTests {

    @Test
    public void parseUriWithoutParams() {
        final ResourceLocation unixRef = new ResourceLocation("file:///tmp/uploads/xyz.jpg");

        assertAll(
                () -> assertEquals("file", unixRef.getProtocol()),
                () -> assertEquals("", unixRef.getVolumeName()),
                () -> assertEquals("/tmp/uploads/xyz.jpg", unixRef.getPath()),
                () -> assertTrue(unixRef.getParameters().isEmpty())
        );

        final ResourceLocation winRef = new ResourceLocation("file://C:/Windows/Temp/uploads/xyz.jpg");

        assertAll(
                () -> assertEquals("file", winRef.getProtocol()),
                () -> assertEquals("C:", winRef.getVolumeName()),
                () -> assertEquals("Windows/Temp/uploads/xyz.jpg", winRef.getPath()),
                () -> assertTrue(winRef.getParameters().isEmpty())
        );
    }

    @Test
    public void parseUriWithParams() {
        final ResourceLocation unixRef = new ResourceLocation("file:///tmp/uploads/xyz.jpg?createAt=2019-01-01&changedAt=2019-01-02");

        assertAll(
                () -> assertEquals("file", unixRef.getProtocol()),
                () -> assertEquals("", unixRef.getVolumeName()),
                () -> assertEquals("/tmp/uploads/xyz.jpg", unixRef.getPath()),
                () -> assertEquals("2019-01-01", unixRef.getParameters().get("createAt")),
                () -> assertEquals("2019-01-02", unixRef.getParameters().get("changedAt"))
        );

        final ResourceLocation winRef = new ResourceLocation("file://C:/Windows/Temp/uploads/xyz.jpg?createAt=2019-01-01&changedAt=2019-01-02");

        assertAll(
                () -> assertEquals("file", winRef.getProtocol()),
                () -> assertEquals("C:", winRef.getVolumeName()),
                () -> assertEquals("Windows/Temp/uploads/xyz.jpg", winRef.getPath()),
                () -> assertEquals("2019-01-01", winRef.getParameters().get("createAt")),
                () -> assertEquals("2019-01-02", winRef.getParameters().get("changedAt"))
        );
    }

    @Test
    public void parseUriWithEncodedParams() {
        final ResourceLocation unixRef = new ResourceLocation("file:///tmp/uploads/xyz.jpg?description=Temp%20image&lang=portugu%C3%AAs");

        assertAll(
                () -> assertEquals("file", unixRef.getProtocol()),
                () -> assertEquals("", unixRef.getVolumeName()),
                () -> assertEquals("/tmp/uploads/xyz.jpg", unixRef.getPath()),
                () -> assertEquals("Temp image", unixRef.getParameters().get("description")),
                () -> assertEquals("português", unixRef.getParameters().get("lang"))
        );

        final ResourceLocation winRef = new ResourceLocation("file://C:/Windows/Temp/uploads/xyz.jpg?description=Temp%20image&lang=portugu%C3%AAs");

        assertAll(
                () -> assertEquals("file", winRef.getProtocol()),
                () -> assertEquals("C:", winRef.getVolumeName()),
                () -> assertEquals("Windows/Temp/uploads/xyz.jpg", winRef.getPath()),
                () -> assertEquals("Temp image", winRef.getParameters().get("description")),
                () -> assertEquals("português", winRef.getParameters().get("lang"))
        );
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
        "file:///tmp/tests/report.txt; file; ''; /tmp/tests/report.txt",
        "file://C:/Windows/Temp/tests/report.txt; file; C:; Windows/Temp/tests/report.txt",
        "tmp://tests/report.txt; tmp; tests; report.txt",
        "local://myapp/reports/march2019.pdf; local; myapp; reports/march2019.pdf",
        "gs://app-images/profile/harry-potter.jpg; gs; app-images; profile/harry-potter.jpg",
        "s3://app-images/profile/harry-potter.jpg; s3; app-images; profile/harry-potter.jpg"
    })
    public void checkUriFormats(String url, String protocol, String volumeName, String path) {
        final ResourceLocation ref = new ResourceLocation(url);

        assertAll(
                () -> assertEquals(url, ref.toString()),
                () -> assertEquals(protocol, ref.getProtocol()),
                () -> assertEquals(volumeName, ref.getVolumeName()),
                () -> assertEquals(path, ref.getPath())
        );
    }

}
