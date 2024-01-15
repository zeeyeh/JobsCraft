package com.zeeyeh.devtoolkit.command.help;

import com.zeeyeh.devtoolkit.command.CommandEntity;
import com.zeeyeh.devtoolkit.command.SubCommandEntity;
import com.zeeyeh.devtoolkit.config.locales.LanguageManager;
import com.zeeyeh.devtoolkit.plugin.SimplePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HelpGenerator {
    private String prefix;
    private String suffix;
    private final List<String> lines = new ArrayList<>();

    public static HelpGenerator builder() {
        return new HelpGenerator();
    }


    public HelpGenerator setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public HelpGenerator setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }


    public HelpGenerator generate(String lineLang) {
        if (!this.prefix.isEmpty()) {
            this.lines.add(this.prefix);
        }
        Set<CommandEntity> commands = SimplePlugin.getCommandManager().getCommands();
        for (CommandEntity command : commands) {
            StringBuilder builder = getHelpLine(lineLang, command);
            this.lines.add(builder.toString());
        }
        if (!this.suffix.isEmpty()) {
            this.lines.add(this.suffix);
        }
        return this;
    }

    private static StringBuilder getHelpLine(String lineLang, CommandEntity command) {
        Set<SubCommandEntity> subCommands = command.getSubCommands();
        StringBuilder builder = new StringBuilder();
        for (SubCommandEntity subCommand : subCommands) {
            LanguageManager languageManager = SimplePlugin.getLanguageManager();
            String helpLine = languageManager.getLang(lineLang);
            builder.append(languageManager.parseParams(
                    helpLine,
                    command.getName(),
                    subCommand.getName(),
                    subCommand.getDescription().description()
            ));
        }
        return builder;
    }

    public List<String> build() {
        return this.lines;
    }
}
