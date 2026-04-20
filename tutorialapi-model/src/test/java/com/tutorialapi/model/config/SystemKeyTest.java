package com.tutorialapi.model.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemKeyTest {

    @Test
    void testDefaultValues() {
        assertEquals("dev", com.tutorialapi.model.config.SystemKey.MODE.getDefaultValue());
        assertEquals("8443", com.tutorialapi.model.config.SystemKey.PORT.getDefaultValue());
    }

    @Test
    void testGetKey() {
        assertEquals("port", com.tutorialapi.model.config.SystemKey.PORT.getKey());
    }

}