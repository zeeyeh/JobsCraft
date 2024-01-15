package com.zeeyeh.jobscraft.commands;

import com.zeeyeh.devtoolkit.annotation.Commander;
import com.zeeyeh.devtoolkit.annotation.SubCommander;
import com.zeeyeh.devtoolkit.command.AbstractCommand;
import com.zeeyeh.devtoolkit.message.Messenger;
import com.zeeyeh.jobscraft.JobsCraft;
import com.zeeyeh.jobscraft.api.JobsCraftLangApi;
import com.zeeyeh.jobscraft.entity.Job;
import com.zeeyeh.jobscraft.entity.JobLevel;
import com.zeeyeh.jobscraft.factory.JobFactory;
import com.zeeyeh.jobscraft.manager.JobLevelManager;
import com.zeeyeh.jobscraft.manager.JobManager;
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
        name = "levels",
        permission = "JobsCraft.commands.levels.*",
        description = "",
        usage = "/<command>"
)
public class JobLevelCommand extends AbstractCommand {

    /**
     * 列举所有职业等级
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "list", permission = "JobsCraft.commands.levels.list", description = "List all jobs", usage = "/<command> list [page]")
    public boolean list(CommandSender sender, String[] args) {
        JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
        List<JobLevel> jobLevels = jobLevelManager.getJobLevels();
        if (jobLevels.size() == 0) {
            Messenger.send(sender, JobsCraftLangApi.translate("player-job-level-list-empty"));
            return true;
        }
        if (args.length > 0) {
            String pageString = args[0];
            int pageNumber = Integer.parseInt(pageString);
            //List<JobLevel> page = ListUtil.getListPaging(pageNumber - 1, 10, jobLevels);
            //ListUtil.
            //putJobInfoLine(sender, page);
        } else {
            //List<JobLevel> page = ListUtil.getListPaging(0, 10, jobLevels);
            //putJobInfoLine(sender, page);
        }
        return true;
    }

    /**
     * 打印职业等级信息行
     *
     * @param sender    接收消息目标
     * @param jobLevels 需要打印的所有职业等级列表
     */
    public void putJobInfoLine(CommandSender sender, List<JobLevel> jobLevels) {
        JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
        String infoHeader = JobsCraftLangApi.translate("player-job-level-info.header");
        if (!infoHeader.isEmpty()) {
            Messenger.send(sender, infoHeader);
        }
        int i = 1;
        for (JobLevel jobLevel : jobLevels) {
            if (jobLevel == null) {
                continue;
            }
            long id = jobLevel.getId();
            String name = jobLevel.getName();
            String title = jobLevel.getTitle();
            long maxExp = jobLevel.getMaxExp();
            long nextId = jobLevel.getNextId();
            JobLevel nextJobLevel = jobLevelManager.getJobLevel(nextId);
            String nextJobLevelName = "null";
            if (nextJobLevel != null) {
                nextJobLevelName = jobLevel.getName();
            }
            List<String> players = jobLevel.getPlayers();
            String line = JobsCraftLangApi.translate("player-job-level-info.line",
                    String.valueOf(id),
                    name,
                    title,
                    String.valueOf(maxExp),
                    nextJobLevelName,
                    String.valueOf(players.size()));
            Messenger.send(sender, i + ". " + line);
            i++;
        }
        String infoFooter = JobsCraftLangApi.translate("player-job-level-info.footer");
        if (!infoFooter.isEmpty()) {
            Messenger.send(sender, infoFooter);
        }
    }

    /**
     * 查询玩家职业等级信息
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "info", permission = "JobsCraft.commands.levels.info", description = "Query player job information", usage = "/<command> info <playerName>")
    public boolean info(CommandSender sender, String[] args) {
        JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
        String playerName = args[0];
        JobLevel jobLevelByPlayer = jobLevelManager.getJobLevelByPlayer(playerName);
        if (jobLevelByPlayer == null) {
            // 未找到玩家的职业等级
            Messenger.send(sender, JobsCraftLangApi.translate("player-job-level-not-exist"));
            return true;
        }
        long id = jobLevelByPlayer.getId();
        String name = jobLevelByPlayer.getName();
        String title = jobLevelByPlayer.getTitle();
        long maxExp = jobLevelByPlayer.getMaxExp();
        long nextId = jobLevelByPlayer.getNextId();
        List<String> players = jobLevelByPlayer.getPlayers();
        String infoHeader = JobsCraftLangApi.translate("player-job-level-info.header");
        if (!infoHeader.isEmpty()) {
            Messenger.send(sender, infoHeader);
        }
        JobLevel jobLevel = jobLevelManager.getJobLevel(nextId);
        String nextJobLevelName = "null";
        if (jobLevel != null) {
            nextJobLevelName = jobLevel.getName();
        }
        String line = JobsCraftLangApi.translate("player-job-level-info.line",
                String.valueOf(id), name, title, String.valueOf(maxExp), nextJobLevelName, String.valueOf(players.size()));
        Messenger.send(sender, line);
        String infoFooter = JobsCraftLangApi.translate("player-job-level-info.footer");
        if (!infoFooter.isEmpty()) {
            Messenger.send(sender, infoFooter);
        }
        return true;
    }

    /**
     * 创建一个职业等级
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "produce", permission = "JobsCraft.commands.levels.produce", description = "Create a job", usage = "/<command> produce <jobName> <jobTitle> <maxExp>")
    public boolean produce(CommandSender sender, String[] args) {
        JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
        String name = args[0];
        String title = args[1];
        String expString = args[2];
        long exp = Long.parseLong(expString);
        if (!JobFactory.checkName(name)) {
            // 名称不符合规则
            Messenger.send(sender, JobsCraftLangApi.translate("job-level-failed-name-non-standard", name));
            return true;
        }
        JobLevel jobLevel = jobLevelManager.getJobLevel(name);
        if (jobLevel != null) {
            // 目标职业等级已存在
            Messenger.send(sender, JobsCraftLangApi.translate("job-level-failed-name-not-exist", name));
            return true;
        }
        if (!jobLevelManager.createJobLevel(name, title, exp)) {
            // 职业等级删除失败
            Messenger.send(sender, JobsCraftLangApi.translate("delete-job-level-failed", name));
            return true;
        }
        Messenger.send(sender, JobsCraftLangApi.translate("delete-job-level-success", name));
        return true;
    }

    /**
     * 销毁一个职业等级
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "destroy", permission = "JobsCraft.commands.levels.destroy", description = "Delete a job", usage = "/<command> destroy <jobName>")
    public boolean destroy(CommandSender sender, String[] args) {
        JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
        String name = args[0];
        if (!JobFactory.checkName(name)) {
            // 名称不符合规则
            Messenger.send(sender, JobsCraftLangApi.translate("job-level-failed-name-non-standard", name));
            return true;
        }
        JobLevel jobLevel = jobLevelManager.getJobLevel(name);
        if (jobLevel == null) {
            // 目标职业等级不存在
            Messenger.send(sender, JobsCraftLangApi.translate("job-level-failed-name-not-exist", name));
            return true;
        }
        if (!jobLevelManager.deleteJobLevel(name)) {
            // 职业等级删除失败
            Messenger.send(sender, JobsCraftLangApi.translate("delete-job-level-failed", name));
            return true;
        }
        Messenger.send(sender, JobsCraftLangApi.translate("delete-job-level-success", name));
        return true;
    }

    /**
     * 使玩家加入一个职业等级
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "blend", permission = "JobsCraft.commands.levels.blend", description = "Join a job", usage = "/<command> blend <playerName> <jobName>")
    public boolean blend(CommandSender sender, String[] args) {
        JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
        JobManager jobManager = JobsCraft.getInstance().getJobManager();
        String playerName = args[0];
        String jobName = args[1];
        String levelName = args[2];
        Job job = jobManager.getJob(jobName);
        if (job == null) {
            // 目标职业等级不存在
            Messenger.send(sender, JobsCraftLangApi.translate("job-level-failed-name-not-exist", jobName));
            return true;
        }
        if (!jobLevelManager.addPlayer(playerName, job.getId(), levelName)) {
            // 退出职业等级失败
            Messenger.send(sender, JobsCraftLangApi.translate("quit-job-level-failed", levelName));
            return true;
        }
        Messenger.send(sender, JobsCraftLangApi.translate("quit-job-level-success", levelName));
        return true;
    }

    /**
     * 使玩家退出一个职业等级
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "detach", permission = "JobsCraft.commands.levels.detach", description = "Quit a job", usage = "/<command> detach <playerName> <jobName> <levelName>")
    public boolean detach(CommandSender sender, String[] args) {
        JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
        JobManager jobManager = JobsCraft.getInstance().getJobManager();
        String playerName = args[0];
        String jobName = args[1];
        String levelName = args[2];
        Job job = jobManager.getJob(jobName);
        if (job == null) {
            // 目标职业等级不存在
            Messenger.send(sender, JobsCraftLangApi.translate("job-level-failed-name-not-exist", jobName));
            return true;
        }
        if (!jobLevelManager.removePlayer(playerName, job.getId(), levelName)) {
            // 退出职业等级失败
            Messenger.send(sender, JobsCraftLangApi.translate("quit-job-level-failed", levelName));
            return true;
        }
        Messenger.send(sender, JobsCraftLangApi.translate("quit-job-level-success", levelName));
        return true;
    }

    /**
     * 清空所有职业等级
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "blank", permission = "JobsCraft.commands.levels.blank", description = "Clear all jobs", usage = "/<command> blank")
    public boolean blank(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Messenger.send(sender, JobsCraftLangApi.translate("clear-confirm"));
            return true;
        }
        if (args[0].equalsIgnoreCase("confirm")) {
            JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
            if (!jobLevelManager.clearJobLevels()) {
                Messenger.send(sender, JobsCraftLangApi.translate("clear-job-level-failed"));
                return true;
            }
            Messenger.send(sender, JobsCraftLangApi.translate("clear-job-level-success"));
        }
        return true;
    }

    /**
     * 给予玩家指定职业等级经验值
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "give", permission = "JobsCraft.commands.levels.give", description = "Clear all jobs", usage = "/<command> give <playerName> <jobName> <number>")
    public boolean give(CommandSender sender, String[] args) {
        String playerName = args[0];
        String jobName = args[1];
        String expString = args[2];
        int exp = Integer.parseInt(expString);
        JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
        JobManager jobManager = JobsCraft.getInstance().getJobManager();
        Job job = jobManager.getJob(jobName);
        if (job == null) {
            // 找不到目标等级
            return true;
        }
        if (exp <= 0) {
            // 给定的经验值不正确
            return true;
        }
        if (!jobLevelManager.giveExp(playerName, job.getId(), exp)) {
            Messenger.send(sender, JobsCraftLangApi.translate("give-job-level-failed"));
            return true;
        }
        Messenger.send(sender, JobsCraftLangApi.translate("give-job-level-success", expString));
        return true;
    }

    /**
     * 扣除玩家指定职业等级经验值
     *
     * @param sender 命令执行者
     * @param args   命令参数
     */
    @SubCommander(name = "take", permission = "JobsCraft.commands.levels.take", description = "Clear all jobs", usage = "/<command> take")
    public boolean take(CommandSender sender, String[] args) {
        String playerName = args[0];
        String jobName = args[1];
        String expString = args[2];
        int exp = Integer.parseInt(expString);
        JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
        JobManager jobManager = JobsCraft.getInstance().getJobManager();
        Job job = jobManager.getJob(jobName);
        if (job == null) {
            // 找不到目标等级
            return true;
        }
        if (exp <= 0) {
            // 给定的经验值不正确
            return true;
        }
        if (!jobLevelManager.takeExp(playerName, job.getId(), exp)) {
            Messenger.send(sender, JobsCraftLangApi.translate("take-job-level-failed"));
            return true;
        }
        Messenger.send(sender, JobsCraftLangApi.translate("take-job-level-success", expString));
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
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            List<String> list = new ArrayList<>();
            for (Player onlinePlayer : onlinePlayers) {
                list.add(onlinePlayer.getName());
            }
            return list;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("take")) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            List<String> list = new ArrayList<>();
            for (Player onlinePlayer : onlinePlayers) {
                list.add(onlinePlayer.getName());
            }
            return list;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("destroy")) {
            List<String> list = new ArrayList<>();
            JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
            List<JobLevel> jobLevels = jobLevelManager.getJobLevels();
            jobLevels.forEach(jobLevel -> list.add(jobLevel.getName()));
            return list;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("blank")) {
            return Collections.singletonList("confirm");
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("blend")) {
            List<String> list = new ArrayList<>();
            JobManager jobManager = JobsCraft.getInstance().getJobManager();
            List<Job> jobs = jobManager.getJobs();
            for (Job job : jobs) {
                list.add(job.getName());
            }
            return list;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("detach")) {
            List<String> list = new ArrayList<>();
            JobManager jobManager = JobsCraft.getInstance().getJobManager();
            List<Job> jobs = jobManager.getJobs();
            for (Job job : jobs) {
                list.add(job.getName());
            }
            return list;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> list = new ArrayList<>();
            JobManager jobManager = JobsCraft.getInstance().getJobManager();
            List<Job> jobs = jobManager.getJobs();
            for (Job job : jobs) {
                list.add(job.getName());
            }
            return list;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("take")) {
            List<String> list = new ArrayList<>();
            JobManager jobManager = JobsCraft.getInstance().getJobManager();
            List<Job> jobs = jobManager.getJobs();
            for (Job job : jobs) {
                list.add(job.getName());
            }
            return list;
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("blend")) {
            List<String> list = new ArrayList<>();
            JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
            List<JobLevel> jobLevels = jobLevelManager.getJobLevels();
            jobLevels.forEach(jobLevel -> list.add(jobLevel.getName()));
            return list;
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("detach")) {
            List<String> list = new ArrayList<>();
            JobLevelManager jobLevelManager = JobsCraft.getInstance().getJobLevelManager();
            List<JobLevel> jobLevels = jobLevelManager.getJobLevels();
            jobLevels.forEach(jobLevel -> list.add(jobLevel.getName()));
            return list;
        }
        return super.onTabComplete(sender, command, label, args);
    }
}
