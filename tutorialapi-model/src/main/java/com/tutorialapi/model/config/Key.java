package com.tutorialapi.model.config;

public interface Key {
    String name();

    default String getKey() {
        return name().toLowerCase().replace("_", ".");
    }
}
