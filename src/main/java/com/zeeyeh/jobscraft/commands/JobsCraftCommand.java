package com.zeeyeh.jobscraft.commands;

import com.zeeyeh.devtoolkit.annotation.Commander;
import com.zeeyeh.devtoolkit.annotation.SubCommander;
import com.zeeyeh.devtoolkit.command.AbstractCommand;
import com.zeeyeh.jobscraft.JobsCraft;
import org.bukkit.command.CommandSender;

@Commander(
        son = true,
        name = "jobscraft",
        permission = "JobsCraft.commands.jobscraft.*",
        description = ""
)
public class JobsCraftCommand extends AbstractCommand {

    @SubCommander(name = "reload", permission = "JobsCraft.commands.jobscraft.reload", description = "Reload plugin", usage = "/<command> reload")
    public boolean list(CommandSender sender, String[] args) {
        JobsCraft.getInstance().reload();
        JobsCraft.getInstance().reloadConfig();
        return true;
    }
}
