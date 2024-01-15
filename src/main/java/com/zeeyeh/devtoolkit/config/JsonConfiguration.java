package com.zeeyeh.devtoolkit.config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zeeyeh.devtoolkit.util.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonConfiguration extends FileConfiguration {

    /**
     * 加载json配置文件内容
     *
     * @param file 目标文件
     */
    public static JsonConfiguration load(File file) {
        return new JsonConfiguration().loadConfiguration(file);
    }

    /**
     * 加载json配置文件内容
     *
     * @param file 目标文件
     */
    @Override
    public JsonConfiguration loadConfiguration(File file) {
        Gson gson = new Gson();
        String content = FileUtil.getContent(file, StandardCharsets.UTF_8);
        Map<String, Object> parse = null;
        try {
            parse = gson.fromJson(content, Map.class);
        } catch (JsonSyntaxException e) {
            e.fillInStackTrace();
            return null;
        }
        JsonConfiguration jsonConfiguration = new JsonConfiguration();
        jsonConfiguration.setMap(parse);
        return jsonConfiguration;
    }

    /**
     * 加载json文件内容
     *
     * @param content 目标内容
     */
    public static JsonConfiguration load(String content) {
        return new JsonConfiguration().loadConfiguration(content);
    }

    /**
     * 加载json文件内容
     *
     * @param content 目标内容
     */
    @Override
    public JsonConfiguration loadConfiguration(String content) {
        Gson gson = new Gson();
        Map<String, Object> parse;
        try {
            parse = gson.fromJson(content, Map.class);
        } catch (JsonSyntaxException e) {
            e.fillInStackTrace();
            return null;
        }
        JsonConfiguration jsonConfiguration = new JsonConfiguration();
        jsonConfiguration.setMap(parse);
        return jsonConfiguration;
    }
}
