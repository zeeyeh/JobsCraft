package com.zeeyeh.devtoolkit.config;

import java.io.File;

public class FileConfiguration extends Configuration {
    public Configuration loadConfiguration(File file) {
        return new FileConfiguration();
    }

    public Configuration loadConfiguration(String content) {
        return new FileConfiguration();
    }
}
