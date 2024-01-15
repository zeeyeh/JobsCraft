package com.zeeyeh.devtoolkit.command;

import com.zeeyeh.devtoolkit.annotation.SubCommander;
import com.zeeyeh.devtoolkit.command.help.HelpGenerator;
import com.zeeyeh.devtoolkit.message.Messenger;
import com.zeeyeh.devtoolkit.plugin.SimplePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandEntity commandEntity = getCommandEntity(command.getName());
        if (commandEntity == null) {
            return true;
        }
        if (args.length < 1 || args[0].equals("help")) {
            renderHelps(sender);
            return true;
        }
        SubCommandEntity subCommandEntity = getSubCommandEntity(args[0], commandEntity.getSubCommands());
        if (subCommandEntity == null) {
            return true;
        }
        SubCommander description = subCommandEntity.getDescription();
        String permission = description.permission();
        if (!sender.hasPermission(permission)) {
            String permissionMessage = description.permissionMessage();
            if (permissionMessage.equals("${default}")) {
                return false;
            }
            if (permissionMessage.startsWith("${") && permissionMessage.endsWith("}") && permissionMessage.contains(":")) {
                permissionMessage = permissionMessage.substring(2, permissionMessage.length() - 1);
                String[] permissionMessageConfig = permissionMessage.split(":");
                String type = permissionMessageConfig[0].trim();
                permissionMessage = switch (type.hashCode()) {
                    case 3314158 -> SimplePlugin.getLanguageManager().getLang(permissionMessageConfig[1]);
                    case 3556653 -> permissionMessageConfig[1];
                    default -> "";
                };
            }
            Messenger.send(sender, permissionMessage);
            return true;
        }
        Method commandMethod = subCommandEntity.getInstance();
        String[] params = new String[args.length - 1];
        if (args.length > 1) {
            params = Arrays.copyOfRange(args, 1, args.length);
        }
        try {
            Class<?> declaringClass = commandMethod.getDeclaringClass();
            Object instance = declaringClass.getDeclaredConstructor().newInstance();
            return (boolean) commandMethod.invoke(instance, sender, params);
        } catch (Exception e) {
            e.fillInStackTrace();
            return true;
        }
    }

    /**
     * 打印指令帮助
     *
     * @param sender 指令执行者
     */
    private static void renderHelps(CommandSender sender) {
        try {
            String helpPrefixKey = SimplePlugin.getConfigManager().getDefaultConfig().getString("help.prefix");
            String helpSuffixKey = SimplePlugin.getConfigManager().getDefaultConfig().getString("help.suffix");
            String helpLineKey = SimplePlugin.getConfigManager().getDefaultConfig().getString("help.line");
            String helpPrefix = SimplePlugin.getLanguageManager().getLang(helpPrefixKey);
            String helpSuffix = SimplePlugin.getLanguageManager().getLang(helpSuffixKey);
            String helpLine = SimplePlugin.getLanguageManager().getLang(helpLineKey);
            HelpGenerator helpGenerator = HelpGenerator.builder();
            if (!helpPrefix.isEmpty()) {
                helpGenerator.setPrefix(helpPrefix);
            }
            if (!helpSuffix.isEmpty()) {
                helpGenerator.setPrefix(helpSuffix);
            }
            if (!helpLine.isEmpty()) {
                helpGenerator.generate(helpLine);
            }
            List<String> helpLines = helpGenerator.build();
            for (String line : helpLines) {
                Messenger.send(sender, line);
            }
        } catch (NullPointerException e) {
            Messenger.send(Bukkit.getConsoleSender(), "[WARN] Language text loss. " + e.getMessage());
        }
    }

    /**
     * 获取指令实体
     *
     * @param command 指令名称
     */
    private CommandEntity getCommandEntity(String command) {
        Set<CommandEntity> commands = SimplePlugin.getCommandManager().getCommands();
        CommandEntity targetEntity = null;
        for (CommandEntity commandEntity : commands) {
            if (commandEntity.getName().equals(command)) {
                targetEntity = commandEntity;
                break;
            }
        }
        return targetEntity;
    }


    /**
     * 获取子指令实体
     *
     * @param command            子指令名称
     * @param subCommandEntities 子指令集合
     */
    private SubCommandEntity getSubCommandEntity(String command, Set<SubCommandEntity> subCommandEntities) {
        SubCommandEntity targetEntity = null;
        for (SubCommandEntity subCommandEntity : subCommandEntities) {
            if (subCommandEntity.getName().equals(command)) {
                targetEntity = subCommandEntity;
                break;
            }
        }
        return targetEntity;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        CommandEntity commandEntity = getCommandEntity(command.getName());
        if (commandEntity == null) {
            return Collections.emptyList();
        }
        Set<SubCommandEntity> subCommands = commandEntity.getSubCommands();
        if (args.length == 1) {
            List<String> commands = new ArrayList<>();
            for (SubCommandEntity subCommand : subCommands) {
                commands.add(subCommand.getName());
            }
            return commands;
        }
        if (args.length > 1) {
            try {
                Constructor<?> constructor = commandEntity.getInstance().getDeclaredConstructor();
                Object o = constructor.newInstance();
                Method tabsMethod = commandEntity.getInstance().getDeclaredMethod("tabs", CommandEntity.class, CommandSender.class, String[].class);
                if (tabsMethod == null) {
                    return List.of("?");
                }
                List<String> object = (List<String>) tabsMethod.invoke(o, commandEntity, sender, args);
                if (object == null) {
                    return List.of("?");
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
        return List.of("?");
    }
}
