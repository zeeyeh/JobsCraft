package com.zeeyeh.devtoolkit.config;

import com.zeeyeh.devtoolkit.util.FileUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class YamlConfiguration extends FileConfiguration {
    /**
     * 加载json配置文件内容
     *
     * @param file 目标文件
     */
    public static YamlConfiguration load(File file) {
        return new YamlConfiguration().loadConfiguration(file);
    }

    /**
     * 加载json配置文件内容
     *
     * @param file 目标文件
     */
    @Override
    public YamlConfiguration loadConfiguration(File file) {
        Yaml yaml = new Yaml();
        String content = FileUtil.getContent(file, StandardCharsets.UTF_8);
        Map<String, Object> parse;
        try {
            parse = yaml.load(content);
        } catch (Exception e) {
            e.fillInStackTrace();
            return null;
        }
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.setMap(parse);
        return yamlConfiguration;
    }

    /**
     * 加载json文件内容
     *
     * @param content 目标内容
     */
    public static YamlConfiguration load(String content) {
        return new YamlConfiguration().loadConfiguration(content);
    }

    /**
     * 加载json文件内容
     *
     * @param content 目标内容
     */
    @Override
    public YamlConfiguration loadConfiguration(String content) {
        Yaml yaml = new Yaml();
        Map<String, Object> parse;
        try {
            parse = yaml.load(content);
        } catch (Exception e) {
            e.fillInStackTrace();
            return null;
        }
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.setMap(parse);
        return yamlConfiguration;
    }
}
