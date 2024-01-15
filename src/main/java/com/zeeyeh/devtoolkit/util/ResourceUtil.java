package com.zeeyeh.devtoolkit.util;

import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.nio.charset.Charset;

public class ResourceUtil {
    public static InputStream getResource(Plugin plugin, String path) {
        return plugin.getResource(path);
    }

    public static String getResourceContent(InputStream inputStream, Charset charset) {
        return FileUtil.getContent(inputStream, charset);
    }
}
