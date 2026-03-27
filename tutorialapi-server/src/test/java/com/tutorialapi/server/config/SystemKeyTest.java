package com.tutorialapi.server.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemKeyTest {

    @Test
    void testDefaultValues() {
        assertEquals("dev", SystemKey.MODE.getDefaultValue());
        assertEquals("8443", SystemKey.PORT.getDefaultValue());
    }

    @Test
    void testGetKey() {
        assertEquals("port", SystemKey.PORT.getKey());
    }

}