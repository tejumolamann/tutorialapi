package com.tutorialapi.server.config;

import java.util.Locale;

public enum SystemKey {
    PORT("8443"),
    MODE("dev");

    private final String defaultValue;

    SystemKey(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getKey() {
        return name().toLowerCase(Locale.ENGLISH).replace("_", ".");
    }
}
