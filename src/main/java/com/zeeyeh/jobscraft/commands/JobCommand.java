package com.zeeyeh.jobscraft.commands;

import com.zeeyeh.devtoolkit.annotation.Commander;
import com.zeeyeh.devtoolkit.annotation.SubCommander;
import com.zeeyeh.devtoolkit.command.AbstractCommand;
import com.zeeyeh.devtoolkit.message.Messenger;
import com.zeeyeh.jobscraft.JobsCraft;
import com.zeeyeh.jobscraft.api.JobsCraftLangApi;
import com.zeeyeh.jobscraft.entity.Job;
import com.zeeyeh.jobscraft.factory.JobFactory;
import com.zeeyeh.jobscraft.manager.JobManager;
import com.zeeyeh.jobscraft.utils.ListUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Commander(
        son = true,
        name = "jobs",
        permission = "JobsCraft.commands.jobs.*",
        description = "",
        usage = "/<command>"
)
public class JobCommand extends AbstractCommand {

    /**
     * 列举所有职业
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "list", permission = "JobsCraft.commands.jobs.list", description = "List all jobs", usage = "/<command> list [page]")
    public boolean list(CommandSender sender, String[] args) {
        JobManager jobManager = JobsCraft.getInstance().getJobManager();
        List<Job> jobs = jobManager.getJobs();
        if (jobs.size() == 0) {
            Messenger.send(sender, JobsCraftLangApi.translate("player-job-list-empty"));
            return true;
        }
        if (args.length > 0) {
            String pageString = args[0];
            int pageNumber = Integer.parseInt(pageString);
            List<Job> page = ListUtil.toPaging(pageNumber, 10, jobs);
            putJobInfoLine(sender, page);
        } else {
            List<Job> page = ListUtil.toPaging(1, 10, jobs);
            putJobInfoLine(sender, page);
        }
        return true;
    }

    /**
     * 打印职业信息行
     *
     * @param sender 接收消息目标
     * @param jobs   需要打印的所有职业列表
     */
    public void putJobInfoLine(CommandSender sender, List<Job> jobs) {
        String infoHeader = JobsCraftLangApi.translate("player-job-info.header");
        if (!infoHeader.isEmpty()) {
            Messenger.send(sender, infoHeader);
        }
        int i = 1;
        for (Job job : jobs) {
            if (job == null) {
                continue;
            }
            long id = job.getId();
            String name = job.getName();
            String title = job.getTitle();
            List<String> players = job.getPlayers();
            String line = JobsCraftLangApi.translate(
                    "player-job-info.line",
                    String.valueOf(id),
                    name,
                    title,
                    String.valueOf(players.size()));
            Messenger.send(sender, i + ". " + line);
            i++;
        }
        String infoFooter = JobsCraftLangApi.translate("player-job-info.footer");
        if (!infoFooter.isEmpty()) {
            Messenger.send(sender, infoFooter);
        }
    }

    /**
     * 查询玩家职业信息
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "info", permission = "JobsCraft.commands.jobs.info", description = "Query player job information", usage = "/<command> info <playerName>")
    public boolean info(CommandSender sender, String[] args) {
        JobManager jobManager = JobsCraft.getInstance().getJobManager();
        String playerName = args[0];
        Job job = jobManager.getJobByPlayer(playerName);
        if (job == null) {
            // 未找到玩家的职业
            Messenger.send(sender, JobsCraftLangApi.translate("player-job-not-exist"));
            return true;
        }
        long id = job.getId();
        String name = job.getName();
        String title = job.getTitle();
        List<String> players = job.getPlayers();
        String infoHeader = JobsCraftLangApi.translate("player-job-info.header");
        if (!infoHeader.isEmpty()) {
            Messenger.send(sender, infoHeader);
        }
        String line = JobsCraftLangApi.translate(
                "player-job-info.line",
                String.valueOf(id),
                name,
                title,
                String.valueOf(players.size()));
        Messenger.send(sender, line);
        String infoFooter = JobsCraftLangApi.translate("player-job-info.footer");
        if (!infoFooter.isEmpty()) {
            Messenger.send(sender, infoFooter);
        }
        return true;
    }

    /**
     * 创建一个职业
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "produce", permission = "JobsCraft.commands.jobs.produce", description = "Create a job", usage = "/<command> produce <jobName> <jobTitle>")
    public boolean produce(CommandSender sender, String[] args) {
        JobManager jobManager = JobsCraft.getInstance().getJobManager();
        String name = args[0];
        if (!JobFactory.checkName(name)) {
            // 名称不符合规则
            Messenger.send(sender, JobsCraftLangApi.translate("job-failed-name-non-standard"), name);
            return true;
        }
        Job job = jobManager.getJob(name);
        if (job != null) {
            // 目标职业已存在
            Messenger.send(sender, JobsCraftLangApi.translate("job-failed-name-already-exists"), name);
            return true;
        }
        String title = args[1];
        if (!jobManager.createJob(name, title)) {
            // 创建失败
            Messenger.send(sender, JobsCraftLangApi.translate("create-job-failed", name));
        }
        Messenger.send(sender, JobsCraftLangApi.translate("create-job-success", name));
        return true;
    }

    /**
     * 销毁一个职业
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "destroy", permission = "JobsCraft.commands.jobs.destroy", description = "Delete a job", usage = "/<command> destroy <jobName>")
    public boolean destroy(CommandSender sender, String[] args) {
        JobManager jobManager = JobsCraft.getInstance().getJobManager();
        String name = args[0];
        if (!JobFactory.checkName(name)) {
            // 名称不符合规则
            Messenger.send(sender, JobsCraftLangApi.translate("job-failed-name-non-standard", name));
            return true;
        }
        Job job = jobManager.getJob(name);
        if (job == null) {
            // 目标职业不存在
            Messenger.send(sender, JobsCraftLangApi.translate("job-failed-name-not-exist", name));
            return true;
        }
        if (!jobManager.deleteJob(name)) {
            // 职业删除失败
            Messenger.send(sender, JobsCraftLangApi.translate("delete-job-failed", name));
            return true;
        }
        Messenger.send(sender, JobsCraftLangApi.translate("delete-job-success", name));
        return true;
    }

    /**
     * 使玩家加入一个职业
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "blend", permission = "JobsCraft.commands.jobs.blend", description = "Join a job", usage = "/<command> blend <playerName> <jobName>")
    public boolean blend(CommandSender sender, String[] args) {
        JobManager jobManager = JobsCraft.getInstance().getJobManager();
        String playerName = args[0];
        String name = args[1];
        if (!JobFactory.checkName(name)) {
            // 名称不符合规则
            Messenger.send(sender, JobsCraftLangApi.translate("job-failed-name-non-standard", name));
            return true;
        }
        Job job = jobManager.getJob(name);
        if (job == null) {
            // 目标职业不存在
            Messenger.send(sender, JobsCraftLangApi.translate("job-failed-name-not-exist", name));
            return true;
        }
        if (!jobManager.addPlayer(playerName, name)) {
            // 加入职业失败
            Messenger.send(sender, JobsCraftLangApi.translate("join-job-failed", name));
            return true;
        }
        Messenger.send(sender, JobsCraftLangApi.translate("join-job-success", name));
        return true;
    }

    /**
     * 使玩家退出一个职业
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "detach", permission = "JobsCraft.commands.jobs.detach", description = "Quit a job", usage = "/<command> detach <playerName> <jobName>")
    public boolean detach(CommandSender sender, String[] args) {
        JobManager jobManager = JobsCraft.getInstance().getJobManager();
        String playerName = args[0];
        String name = args[1];
        if (!JobFactory.checkName(name)) {
            // 名称不符合规则
            Messenger.send(sender, JobsCraftLangApi.translate("job-failed-name-non-standard", name));
            return true;
        }
        Job job = jobManager.getJob(name);
        if (job == null) {
            // 目标职业不存在
            Messenger.send(sender, JobsCraftLangApi.translate("job-failed-name-not-exist", name));
            return true;
        }
        if (!jobManager.removePlayer(playerName, name)) {
            // 退出职业失败
            Messenger.send(sender, JobsCraftLangApi.translate("quit-job-failed", name));
            return true;
        }
        Messenger.send(sender, JobsCraftLangApi.translate("quit-job-success", name));
        return true;
    }

    /**
     * 清空所有职业
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "blank", permission = "JobsCraft.commands.jobs.blank", description = "Clear all jobs", usage = "/<command> blank")
    public boolean blank(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Messenger.send(sender, JobsCraftLangApi.translate("clear-confirm"));
            return true;
        }
        if (args[0].equalsIgnoreCase("confirm")) {
            JobManager jobManager = JobsCraft.getInstance().getJobManager();
            if (!jobManager.clearJobs()) {
                Messenger.send(sender, JobsCraftLangApi.translate("clear-job-failed"));
                return true;
            }
            Messenger.send(sender, JobsCraftLangApi.translate("clear-job-success"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            List<String> list = new ArrayList<>();
            for (Player onlinePlayer : onlinePlayers) {
                list.add(onlinePlayer.getName());
            }
            return list;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("blend")) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            List<String> list = new ArrayList<>();
            for (Player onlinePlayer : onlinePlayers) {
                list.add(onlinePlayer.getName());
            }
            return list;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("detach")) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            List<String> list = new ArrayList<>();
            for (Player onlinePlayer : onlinePlayers) {
                list.add(onlinePlayer.getName());
            }
            return list;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("destroy")) {
            List<String> list = new ArrayList<>();
            JobManager jobManager = JobsCraft.getInstance().getJobManager();
            List<Job> jobs = jobManager.getCacheJobs();
            for (Job job : jobs) {
                list.add(job.getName());
            }
            return list;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("blank")) {
            return Collections.singletonList("confirm");
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("blend")) {
            List<String> list = new ArrayList<>();
            JobManager jobManager = JobsCraft.getInstance().getJobManager();
            List<Job> jobs = jobManager.getCacheJobs();
            for (Job job : jobs) {
                list.add(job.getName());
            }
            return list;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("detach")) {
            List<String> list = new ArrayList<>();
            JobManager jobManager = JobsCraft.getInstance().getJobManager();
            List<Job> jobs = jobManager.getCacheJobs();
            for (Job job : jobs) {
                list.add(job.getName());
            }
            return list;
        }
        return super.onTabComplete(sender, command, label, args);
    }
}
