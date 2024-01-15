package com.zeeyeh.devtoolkit.command;

import com.zeeyeh.devtoolkit.annotation.Commander;
import com.zeeyeh.devtoolkit.annotation.SubCommander;
import com.zeeyeh.devtoolkit.util.ReflectionUtil;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class CommandManager {
    private final Plugin plugin;
    private final String basePackage;
    private Set<CommandEntity> commands;

    /**
     * @param plugin      插件主类
     * @param basePackage 插件主包名。存放所有与插件相关的类及包下的子类的包路径
     */
    public CommandManager(Plugin plugin, String basePackage) {
        this.plugin = plugin;
        basePackage = basePackage.startsWith(".") ? basePackage.substring(1) : basePackage;
        basePackage = basePackage.endsWith(".") ? basePackage.substring(0, basePackage.length() - 1) : basePackage;
        this.basePackage = basePackage;
    }

    /**
     * 初始化所有
     *
     * @param commandPackagePath 相对于插件主包名级别的所有指令类存放的父包路径
     */
    public CommandManager initializeCommands(String commandPackagePath) {
        commandPackagePath = commandPackagePath.startsWith(".") ? commandPackagePath.substring(1) : commandPackagePath;
        commandPackagePath = commandPackagePath.endsWith(".") ? commandPackagePath.substring(0, commandPackagePath.length() - 1) : commandPackagePath;
        String packagePath = getBasePackage() + "." + commandPackagePath;
        Set<Class<?>> commanders = ReflectionUtil.findCommanders(getPlugin(), packagePath);
        return parseCommanders(commanders);
    }

    /**
     * 解析所有指令提供类
     *
     * @param commanders 所有指令提供类集合
     */
    public CommandManager parseCommanders(Set<Class<?>> commanders) {
        try {
            this.commands = new LinkedHashSet<>();
            for (Class<?> commanderClass : commanders) {
                Commander commanderClassAnnotation = commanderClass.getAnnotation(Commander.class);
                String commandName = commanderClassAnnotation.name();
                if (commanderClassAnnotation.son()) {
                    // 包含子指令
                    Set<Method> subCommander = ReflectionUtil.findSubCommander(commanderClass);
                    Set<SubCommandEntity> subCommandEntities = new LinkedHashSet<>();
                    parseSubCommanders(subCommander, subCommandEntities);
                    this.commands.add(new CommandEntity(
                            commandName,
                            commanderClass,
                            subCommandEntities,
                            commanderClassAnnotation
                    ));
                } else {
                    this.commands.add(new CommandEntity(
                            commandName,
                            commanderClass,
                            new HashSet<>(),
                            commanderClassAnnotation
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Directive provision class resolution failed: " + e);
        }
        return this;
    }

    /**
     * 解析所有子指令
     *
     * @param subCommander 所有子指令集合
     * @param commands     存放所有子指令的集合
     */
    public void parseSubCommanders(Set<Method> subCommander, Set<SubCommandEntity> commands) {
        for (Method method : subCommander) {
            SubCommander subCommanderMethodAnnotation = method.getAnnotation(SubCommander.class);
            commands.add(new SubCommandEntity(
                    subCommanderMethodAnnotation.name(),
                    method,
                    subCommanderMethodAnnotation
            ));
        }
    }

    /**
     * 获取插件主包名
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * 获取插件主类
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * 获取所有指令信息
     */
    public Set<CommandEntity> getCommands() {
        return commands;
    }
}
