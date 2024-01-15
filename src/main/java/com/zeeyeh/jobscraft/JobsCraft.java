package com.zeeyeh.jobscraft;

import com.zeeyeh.devtoolkit.annotation.AutoRegistration;
import com.zeeyeh.devtoolkit.annotation.PluginBootstrap;
import com.zeeyeh.devtoolkit.command.CommandEntity;
import com.zeeyeh.devtoolkit.command.CommandManager;
import com.zeeyeh.devtoolkit.config.ConfigManager;
import com.zeeyeh.devtoolkit.config.locales.LanguageManager;
import com.zeeyeh.devtoolkit.message.Messenger;
import com.zeeyeh.devtoolkit.plugin.SimplePlugin;
import com.zeeyeh.jobscraft.api.JobsCraftLangApi;
import com.zeeyeh.jobscraft.database.SimpleDatabase;
import com.zeeyeh.jobscraft.manager.JobCurtailManager;
import com.zeeyeh.jobscraft.manager.JobLevelManager;
import com.zeeyeh.jobscraft.manager.JobManager;
import com.zeeyeh.jobscraft.utils.LibrariesUtil;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@PluginBootstrap
@AutoRegistration
public class JobsCraft extends SimplePlugin {
    public JobsCraft() {
        Logger Log = Logger.getLogger("com.alibaba.druid.support.logging.Log");
        Log.setUseParentHandlers(false);
        Log.setLevel(Level.OFF);
        Logger LogFactory = Logger.getLogger("com.alibaba.druid.support.logging.LogFactory");
        LogFactory.setUseParentHandlers(false);
        LogFactory.setLevel(Level.OFF);
        Logger logger = Logger.getLogger("com.alibaba.druid.pool.DruidDataSource");
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.OFF);
        this.setBasePackage("com.zeeyeh.jobscraft");
        this.libraries = new ArrayList<>();
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
            }
        }));
    }

    private SimpleDatabase database;
    private JobManager jobManager;
    private JobLevelManager jobLevelManager;
    private JobCurtailManager jobCurtailManager;
    private final List<File> libraries;

    @Override
    public void enable() {
        getConfigManager().initializeDefaultConfig();
        String locale = getConfigManager().getDefaultConfig().getString("locale");
        setLanguageManager(new LanguageManager(this, locale));
        File localeFolder = new File(getDataFolder(), "locales");
        if (!localeFolder.exists()) {
            if (!localeFolder.mkdirs()) {
                Messenger.send(Bukkit.getConsoleSender(), "Language directory creation failed");
            }
        }
        getLanguageManager().initializeDefaultLang();
        JobsCraftLangApi.initLanguage(getLanguageManager());
        loadLibraries();
        this.database = new SimpleDatabase();
        this.jobManager = new JobManager();
        this.jobManager.initialize();
        this.jobLevelManager = new JobLevelManager();
        this.jobLevelManager.initialize();
        this.jobCurtailManager = new JobCurtailManager();
        this.jobCurtailManager.initialize();
        getLogger().info("加载指令: ");
        Set<CommandEntity> commands = getCommandManager().getCommands();
        for (CommandEntity command : commands) {
            getLogger().info("加載指令： " + command.getName());
        }
        getLogger().info("指令加载完成");
        //Bukkit.getPluginManager().registerEvents(new JobPlayerListener(), this);
    }

    public SimpleDatabase getDatabase() {
        return database;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public static JobsCraft getInstance() {
        return (JobsCraft) SimplePlugin.getInstance();
    }

    public JobLevelManager getJobLevelManager() {
        return jobLevelManager;
    }

    public JobCurtailManager getJobCurtailManager() {
        return jobCurtailManager;
    }

    public static ConfigManager getConfigManager() {
        return SimplePlugin.getConfigManager();
    }

    public static LanguageManager getLanguageManager() {
        return SimplePlugin.getLanguageManager();
    }

    public static CommandManager getCommandManager() {
        return SimplePlugin.getCommandManager();
    }

    public void reload() {
        String locale = getConfigManager().getDefaultConfig().getString("locale");
        getLanguageManager().setLangName(locale);
        getLanguageManager().clear();
        getLanguageManager().loadAll();
    }

    @Override
    public void disable() {
    }

    public void loadLibraries() {
        File libraries = new File(System.getProperty("user.dir"), "libraries");
        this.libraries.add(LibrariesUtil.loadFile("com.mysql", "mysql-connector-j", "8.2.0", libraries));
        this.libraries.add(LibrariesUtil.loadFile("org.postgresql", "postgresql", "42.7.1", libraries));
        this.libraries.add(LibrariesUtil.loadFile("org.xerial", "sqlite-jdbc", "3.44.1.0", libraries));
    }

    public List<File> getLibraries() {
        return libraries;
    }

    @Override
    public String getCommandsModelName() {
        return "commands";
    }
}
