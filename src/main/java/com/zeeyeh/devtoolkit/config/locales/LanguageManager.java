package com.zeeyeh.devtoolkit.config.locales;


import com.zeeyeh.devtoolkit.config.Configuration;
import com.zeeyeh.devtoolkit.config.FileConfiguration;
import com.zeeyeh.devtoolkit.config.YamlConfiguration;
import com.zeeyeh.devtoolkit.message.Messenger;
import com.zeeyeh.devtoolkit.util.ResourceUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class LanguageManager {
    private final Map<String, Configuration> langConfigs;
    private final Plugin plugin;
    private final String suffix;
    public static final String DEFAULT_CONFIG_SUFFIX = "yml";
    private String langName;
    private FileConfiguration configurationHandler;
    private static final String DEFAULT_LANG_NAME = "en_us";

    public LanguageManager(Plugin plugin) {
        this(plugin, DEFAULT_CONFIG_SUFFIX, DEFAULT_LANG_NAME);
    }

    public LanguageManager(Plugin plugin, String langName) {
        this(plugin, DEFAULT_CONFIG_SUFFIX, langName);
    }

    public LanguageManager(Plugin plugin, String suffix, String langName) {
        this(plugin, suffix, langName, new YamlConfiguration());
    }

    public LanguageManager(Plugin plugin, String suffix, String langName, FileConfiguration configurationHandler) {
        langConfigs = new HashMap<>();
        this.plugin = plugin;
        this.suffix = suffix;
        this.langName = langName;
        this.configurationHandler = configurationHandler;
    }

    /**
     * 初始化默认语言文件
     */
    public void initializeDefaultLang() {
        InputStream resource = ResourceUtil.getResource(getPlugin(), "locales/en_us.yml");
        File file = new File(getPlugin().getDataFolder(), "locales/en_us.yml");
        if (file.exists()) {
            loadLang("en_us");
            return;
        }
        if (resource != null) {
            try {
                Files.copy(resource, file.toPath());
            } catch (IOException e) {
                e.fillInStackTrace();
            }
            loadLang("en_us");
        }
    }

    /**
     * 加载语言文件
     *
     * @param configName 语言文件名称
     */
    public void loadLang(String configName) {
        File file = new File(getPlugin().getDataFolder(), "locales/" + configName + "." + getSuffix());
        if (!file.exists()) {
            Messenger.send(Bukkit.getConsoleSender(), "Language file " + configName + ".yml is missing");
            return;
        }
        Configuration configuration = getConfigurationHandler().loadConfiguration(file);
        this.langConfigs.put(configName, configuration);
    }

    /**
     * 获取插件主类
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * 获取语言文件后缀名
     */
    public String getSuffix() {
        return suffix;
    }

    public String parseParams(String content, String... params) {
        for (int i = 0; i < params.length; i++) {
            content = content.replace("{" + i + "}", params[i]);
        }
        return content;
    }

    /**
     * 获取语言
     */
    public String getLang(String path) {
        if (!this.langConfigs.containsKey(getLangName())) {
            throw new NoSuchElementException("The target language was not found. Please check the language name. lang: " + getLangName());
        }
        Configuration configuration = this.langConfigs.get(getLangName());
        if (configuration == null) {
            return "";
        }
        return configuration.getString(path);
    }

    /**
     * 获取所有语言
     */
    public Map<String, Configuration> getLangConfigs() {
        return this.langConfigs;
    }

    /**
     * 设置语言处理器
     *
     * @param configurationHandler 语言处理器类
     */
    public void setConfigurationHandler(FileConfiguration configurationHandler) {
        this.configurationHandler = configurationHandler;
    }

    /**
     * 获取语言处理器
     */
    public FileConfiguration getConfigurationHandler() {
        return configurationHandler;
    }

    /**
     * 获取语言名称
     */
    public String getLangName() {
        return langName;
    }

    /**
     * 设置语言名称
     *
     * @param langName 语言名称
     */
    public void setLangName(String langName) {
        this.langName = langName;
    }

    public void clear() {
        this.langConfigs.clear();
    }

    public void loadAll() {
        File folder = new File(getPlugin().getDataFolder(), "locales");
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            this.loadLang(file.getName().substring(0, file.getName().indexOf(".")));
        }
    }
}
