package com.github.microtweak.jvolumes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceLocationTests {

    @Test
    public void parseUri() {
        ResourceLocation ref = new ResourceLocation("file://tmp/uploads/xyz.jpg");

        assertAll(
                () -> assertEquals(ref.getProtocol(), "file"),
                () -> assertEquals(ref.getVolumeName(), "tmp"),
                () -> assertEquals(ref.getPath(), "uploads/xyz.jpg"),
                () -> assertTrue(ref.getParameters().isEmpty())
        );
    }

    @Test
    public void parseUriWithParams() {
        ResourceLocation ref = new ResourceLocation("file://tmp/uploads/xyz.jpg?createAt=2019-01-01&changedAt=2019-01-02");

        assertAll(
                () -> assertEquals(ref.getProtocol(), "file"),
                () -> assertEquals(ref.getVolumeName(), "tmp"),
                () -> assertEquals(ref.getPath(), "uploads/xyz.jpg"),
                () -> assertEquals(ref.getParameters().get("createAt"), "2019-01-01"),
                () -> assertEquals(ref.getParameters().get("changedAt"), "2019-01-02")
        );
    }

    @Test
    public void parseUriWithEncodedParams() {
        ResourceLocation ref = new ResourceLocation("file://tmp/uploads/xyz.jpg?description=Temp%20image&lang=portugu%C3%AAs");

        assertAll(
                () -> assertEquals(ref.getProtocol(), "file"),
                () -> assertEquals(ref.getVolumeName(), "tmp"),
                () -> assertEquals(ref.getPath(), "uploads/xyz.jpg"),
                () -> assertEquals(ref.getParameters().get("description"), "Temp image"),
                () -> assertEquals(ref.getParameters().get("lang"), "português")
        );
    }

}
