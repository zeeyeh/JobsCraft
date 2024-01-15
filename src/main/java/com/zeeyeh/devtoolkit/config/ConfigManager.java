package com.zeeyeh.devtoolkit.config;


import com.zeeyeh.devtoolkit.util.ResourceUtil;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final Map<String, Configuration> configs;
    private final Plugin plugin;
    private final String suffix;
    public static final String DEFAULT_CONFIG_SUFFIX = "yml";
    private FileConfiguration configurationHandler;

    public ConfigManager(Plugin plugin) {
        this(plugin, DEFAULT_CONFIG_SUFFIX);
    }

    public ConfigManager(Plugin plugin, String suffix) {
        this(plugin, suffix, new YamlConfiguration());
    }

    public ConfigManager(Plugin plugin, String suffix, FileConfiguration configurationHandler) {
        configs = new HashMap<>();
        this.plugin = plugin;
        this.suffix = suffix;
        this.configurationHandler = configurationHandler;
    }

    /**
     * 初始化默认配置文件
     */
    public void initializeDefaultConfig() {
        InputStream resource = ResourceUtil.getResource(getPlugin(), "config.yml");
        if (resource != null) {
            getPlugin().saveDefaultConfig();
            loadConfig("config");
        }
    }

    /**
     * 加载配置文件
     *
     * @param configName 配置文件名称
     */
    public void loadConfig(String configName) {
        File file = new File(getPlugin().getDataFolder(), configName + "." + getSuffix());
        Configuration configuration = getConfigurationHandler().loadConfiguration(file);
        this.configs.put(configName, configuration);
    }

    /**
     * 获取插件主类
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * 获取配置文件后缀名
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * 获取默认配置
     */
    public Configuration getDefaultConfig() {
        Configuration configuration = new FileConfiguration();
        if (this.configs.containsKey("config")) {
            configuration = this.configs.get("config");
        }
        return configuration;
    }

    /**
     * 获取所有配置
     */
    public Map<String, Configuration> getConfigs() {
        return this.configs;
    }

    /**
     * 设置配置处理器
     *
     * @param configurationHandler 配置处理器类
     */
    public void setConfigurationHandler(FileConfiguration configurationHandler) {
        this.configurationHandler = configurationHandler;
    }

    /**
     * 获取配置处理器
     */
    public FileConfiguration getConfigurationHandler() {
        return configurationHandler;
    }

    public void clear() {
        this.configs.clear();
    }
}
