package com.zeeyeh.devtoolkit.config;

import java.util.Map;

public class MapConfiguration extends Configuration {
    public static MapConfiguration loadConfiguration(Map<String, Object> map) {
        MapConfiguration mapConfiguration = new MapConfiguration();
        mapConfiguration.setMap(map);
        return mapConfiguration;
    }
}
