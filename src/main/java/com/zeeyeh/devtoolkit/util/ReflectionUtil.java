package com.zeeyeh.devtoolkit.util;

import com.zeeyeh.devtoolkit.annotation.Commander;
import com.zeeyeh.devtoolkit.annotation.PluginBootstrap;
import com.zeeyeh.devtoolkit.annotation.SubCommander;
import com.zeeyeh.devtoolkit.command.AbstractCommand;
import com.zeeyeh.devtoolkit.plugin.SimplePlugin;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionUtil {

    /**
     * 扫描插件主类
     *
     * @param plugin      插件实例
     * @param packagePath 根包路径
     */
    public static Class<?> findPluginClass(Plugin plugin, String packagePath) {
        String packageName = packagePath.replace('/', '.');
        try {
            Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            File file = (File) getFileMethod.invoke(plugin);
            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String classname = jarEntry.getName().replace('/', '.');
                    if (classname.startsWith(packageName) && classname.endsWith(".class")) {
                        classname = classname.substring(0, classname.length() - 6);
                        Class<?> aClass = Class.forName(classname);
                        if (SimplePlugin.class.isAssignableFrom(aClass)) {
                            if (aClass.isAnnotationPresent(PluginBootstrap.class)) {
                                return aClass;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("An exception occurred to scan the main class: " + e);
        }
        return null;
    }

    /**
     * 扫描所有事件监听器类
     *
     * @param plugin      插件主类实例
     * @param packagePath 指令存放父包路径
     */
    public static Set<Class<?>> findListeners(Plugin plugin, String packagePath) {
        String packageName = packagePath.replace('/', '.');
        Set<Class<?>> classes = new LinkedHashSet<>();
        try {
            Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            File file = (File) getFileMethod.invoke(plugin);
            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String classname = jarEntry.getName().replace('/', '.');
                    if (classname.startsWith(packageName) && classname.endsWith(".class")) {
                        classname = classname.substring(0, classname.length() - 6);
                        Class<?> aClass = Class.forName(classname);
                        if (!Listener.class.isAssignableFrom(aClass)) {
                            continue;
                        }
                        classes.add(aClass);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred to scan the main class: " + e);
        }
        return classes;
    }

    /**
     * 扫描所有指令提供类
     *
     * @param plugin      插件主类实例
     * @param packagePath 指令存放父包路径
     */
    public static Set<Class<?>> findCommanders(Plugin plugin, String packagePath) {
        String packageName = packagePath.replace('/', '.');
        Set<Class<?>> classes = new LinkedHashSet<>();
        try {
            Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            File file = (File) getFileMethod.invoke(plugin);
            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String classname = jarEntry.getName().replace('/', '.');
                    if (classname.startsWith(packageName) && classname.endsWith(".class")) {
                        classname = classname.substring(0, classname.length() - 6);
                        Class<?> aClass = Class.forName(classname);
                        if (!aClass.isAnnotationPresent(Commander.class)) {
                            continue;
                        }
                        if (!AbstractCommand.class.isAssignableFrom(aClass)) {
                            continue;
                        }
                        classes.add(aClass);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred to scan the main class: " + e);
        }
        return classes;
    }

    /**
     * 扫描指令提供类中所有子指令提供方法
     *
     * @param commander 指令提供类实例
     */
    public static Set<Method> findSubCommander(Class<?> commander) {
        Set<Method> methods = new LinkedHashSet<>();
        Method[] declaredMethods = commander.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (!declaredMethod.isAnnotationPresent(SubCommander.class)) {
                continue;
            }
            if (!(declaredMethod.getReturnType().getTypeName().equalsIgnoreCase("boolean"))) {
                continue;
            }
            Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
            if (parameterTypes.length != 2) {
                continue;
            }
            if (!parameterTypes[0].getTypeName().equalsIgnoreCase("org.bukkit.command.CommandSender")) {
                continue;
            }
            if (!parameterTypes[1].getTypeName().equalsIgnoreCase("java.lang.String[]")) {
                continue;
            }
            methods.add(declaredMethod);
        }
        return methods;
    }
}
