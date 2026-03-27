package com.tutorialapi.server.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigKeyTest {

    @Test
    void testGetKey() {
        assertEquals("server.keystore.file", ConfigKey.SERVER_KEYSTORE_FILE.getKey(),
                "Key should be converted to lower case and spaces should be replaced with dots");
    }
}
