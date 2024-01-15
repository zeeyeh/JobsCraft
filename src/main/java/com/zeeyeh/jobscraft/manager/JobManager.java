package com.zeeyeh.jobscraft.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.zeeyeh.devtoolkit.database.DBEntity;
import com.zeeyeh.jobscraft.JobsCraft;
import com.zeeyeh.jobscraft.database.SimpleDatabase;
import com.zeeyeh.jobscraft.entity.DataSaveType;
import com.zeeyeh.jobscraft.entity.Job;
import com.zeeyeh.jobscraft.entity.JobsCraftTableNames;
import com.zeeyeh.jobscraft.event.job.JobCreateEvent;
import com.zeeyeh.jobscraft.factory.JobFactory;
import com.zeeyeh.jobscraft.utils.DbUtil;
import com.zeeyeh.jobscraft.utils.JsonUtil;
import com.zeeyeh.jobscraft.utils.MapUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 职业管理器
 */
public class JobManager extends CraftManager {
    private List<Job> cacheJobs;

    public JobManager() {
        cacheJobs = new ArrayList<>();
    }

    /**
     * 初始化职业管理器
     */
    public void initialize() {
        String saveType = JobsCraft.getConfigManager().getDefaultConfig().getString("saveType");
        if (saveType.equalsIgnoreCase("database")) {
            // 数据库方式存储
            setType(DataSaveType.DATABASE);
            SimpleDatabase database = JobsCraft.getInstance()
                    .getDatabase();
            setDataSource(database.getDataSource(database.getDatatype(), null));
        } else if (saveType.equalsIgnoreCase("file")) {
            // 本地文件方式存储
            setType(DataSaveType.LOCAL);
            setFolder(new File(JobsCraft.getInstance().getDataFolder(), "jobs"));
            if (!getFolder().exists()) {
                getFolder().mkdirs();
            }
        }
        this.cacheJobs = getJobs();
    }

    public void reload() {
        this.cacheJobs.clear();
        initialize();
    }

    public List<Job> getCacheJobs() {
        return cacheJobs;
    }

    /**
     * 获取所有职业
     */
    public List<Job> getJobs() {
        List<Job> list = new ArrayList<>();
        switch (getType()) {
            case DATABASE -> {
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB.getName(), getDataSource()).findAll();
                for (DBEntity entity : entities) {
                    Long id = entity.getLong("id");
                    String name = entity.getString("name");
                    String title = entity.getString("title");
                    JsonArray playerArrays = JsonUtil.toJsonArray(entity.getString("players"));
                    List<String> players = new ArrayList<String>();
                    for (JsonElement playerArray : playerArrays) {
                        players.add(playerArray.getAsString());
                    }
                    list.add(new Job(id, name, title, players));
                }
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return Collections.emptyList();
                }
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection jobSection = configuration.getConfigurationSection("job");
                    if (jobSection == null) {
                        continue;
                    }
                    long id = jobSection.getLong("id");
                    String name = jobSection.getString("name");
                    String title = jobSection.getString("title");
                    List<String> players = jobSection.getStringList("players");
                    list.add(new Job(id, name, title, players));
                }
            }
        }
        return list;
    }

    /**
     * 根据职业id获取职业信息
     *
     * @param jobId 职业Id
     */
    public Job getJob(long jobId) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("id", jobId);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                JsonArray playerArrays = JsonUtil.toJsonArray(searchEntity.getString("players"));
                List<String> players = new ArrayList<>();
                for (JsonElement jsonElement : playerArrays) {
                    players.add(jsonElement.getAsString());
                }
                return new Job(searchEntity.getLong("id"), searchEntity.getString("name"), searchEntity.getString("title"), players);
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return null;
                }
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection jobSection = configuration.getConfigurationSection("job");
                    if (jobSection == null) {
                        return null;
                    }
                    long id = jobSection.getLong("id");
                    if (id == jobId) {
                        String name = jobSection.getString("name");
                        String title = jobSection.getString("title");
                        List<String> players = jobSection.getStringList("players");
                        return new Job(id, name, title, players);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 通过职业名称获取职业数据
     *
     * @param jobName 职业名称
     */
    public Job getJob(String jobName) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity nameEntity = new DBEntity();
                nameEntity.set("name", jobName);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB.getName(), getDataSource()).find(nameEntity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                JsonArray playerArrays = JsonUtil.toJsonArray(searchEntity.getString("players"));
                List<String> players = new ArrayList<>();
                for (JsonElement jsonElement : playerArrays) {
                    players.add(jsonElement.getAsString());
                }
                return new Job(searchEntity.getLong("id"), searchEntity.getString("name"), searchEntity.getString("title"), players);
            }
            case LOCAL -> {
                File jobFile = new File(getFolder(), jobName + ".yml");
                if (!jobFile.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(jobFile);
                ConfigurationSection jobSection = configuration.getConfigurationSection("job");
                if (jobSection == null) {
                    return null;
                }
                String name = jobSection.getString("name");
                if (name == null) {
                    return null;
                }
                if (!name.equals(jobName)) {
                    return null;
                }
                long id = jobSection.getLong("id");
                String title = jobSection.getString("title");
                List<String> players = jobSection.getStringList("players");
                return new Job(id, name, title, players);
            }
        }
        return null;
    }

    /**
     * 根据玩家对象获取职业信息
     *
     * @param player 玩家对象
     */
    public Job getJobByPlayer(Player player) {
        return getJobByPlayer(player.getName());
    }

    /**
     * 根据玩家名称获取职业信息
     *
     * @param playerName 玩家名称
     */
    public Job getJobByPlayer(String playerName) {
        switch (getType()) {
            case DATABASE:
                DBEntity entity = new DBEntity();
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB.getName(), getDataSource()).findAll();
                if (entities.size() == 0) {
                    return null;
                }
                for (DBEntity searchEntity : entities) {
                    JsonArray playerArrays = JsonUtil.toJsonArray(searchEntity.getString("players"));
                    List<String> players = new ArrayList<>();
                    for (JsonElement jsonElement : playerArrays) {
                        players.add(jsonElement.getAsString());
                    }
                    if (!players.contains(playerName)) {
                        continue;
                    }
                    return new Job(searchEntity.getLong("id"), searchEntity.getString("name"), searchEntity.getString("title"), players);
                }
            case LOCAL:
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return null;
                }
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection jobSection = configuration.getConfigurationSection("job");
                    if (jobSection == null) {
                        return null;
                    }
                    List<String> players = jobSection.getStringList("players");
                    if (!players.contains(playerName)) {
                        return null;
                    }
                    long id = jobSection.getLong("id");
                    String name = jobSection.getString("name");
                    String title = jobSection.getString("title");
                    return new Job(id, name, title, players);
                }
                break;
        }
        return null;
    }

    public boolean addPlayer(String playerName, String jobName) {
        switch (getType()) {
            case DATABASE -> {
                Job job = getJob(jobName);
                if (job == null) {
                    return false;
                }
                List<String> players = job.getPlayers();
                if (players.contains(playerName)) {
                    return false;
                }
                players.add(playerName);
                DBEntity entity = new DBEntity();
                entity.set("players", JsonUtil.toJsonArrayString(players));
                DBEntity where = new DBEntity();
                where.set("name", job.getName());
                DbUtil.builder(JobsCraftTableNames.JOB.getName(), getDataSource()).update(entity, where);
                return true;
            }
            case LOCAL -> {
                File file = new File(getFolder(), jobName + ".yml");
                if (!file.exists()) {
                    return false;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection jobSection = configuration.getConfigurationSection("job");
                if (jobSection == null) {
                    return false;
                }
                List<String> jobPlayers = jobSection.getStringList("players");
                if (jobPlayers.contains(playerName)) {
                    return false;
                }
                jobPlayers.add(playerName);
                jobSection.set("players", jobPlayers);
                configuration.set("job", jobSection);
                try {
                    configuration.save(file);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public boolean removePlayer(String playerName, String jobName) {
        switch (getType()) {
            case DATABASE -> {
                Job job = getJob(jobName);
                if (job == null) {
                    return false;
                }
                List<String> players = job.getPlayers();
                if (!players.contains(playerName)) {
                    return false;
                }
                players.remove(playerName);
                DBEntity entity = new DBEntity();
                entity.set("players", JsonUtil.toJsonArrayString(players));
                DBEntity where = new DBEntity();
                where.set("name", job.getName());
                DbUtil.builder(JobsCraftTableNames.JOB.getName(), getDataSource()).update(entity, where);
                return true;
            }
            case LOCAL -> {
                File file = new File(getFolder(), jobName + ".yml");
                if (!file.exists()) {
                    return false;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection jobSection = configuration.getConfigurationSection("job");
                if (jobSection == null) {
                    return false;
                }
                List<String> jobPlayers = jobSection.getStringList("players");
                if (!jobPlayers.contains(playerName)) {
                    return false;
                }
                jobPlayers.remove(playerName);
                jobSection.set("players", jobPlayers);
                configuration.set("job", jobSection);
                try {
                    configuration.save(file);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 创建一个职业
     *
     * @param name  职业名称
     * @param title 职业标题
     * @return 是否成功
     */
    public boolean createJob(String name, String title) {
        Job job = JobFactory.createJob(name, title);
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("id", job.getId());
                entity.set("name", job.getName());
                entity.set("title", job.getTitle());
                entity.set("players", JsonUtil.toJsonArrayString(job.getPlayers()));
                DbUtil.builder(JobsCraftTableNames.JOB.getName(), getDataSource()).insert(entity);
                Bukkit.getPluginManager().callEvent(new JobCreateEvent(job.getId(), job.getName(), job.getTitle()));
            }
            case LOCAL -> {
                File jobFile = new File(getFolder(), name + ".yml");
                if (jobFile.exists()) {
                    // 已存在
                    return false;
                }
                Yaml yaml = new Yaml();
                Map<String, Object> map = MapUtil.asMap("job", job);
                try {
                    yaml.dump(map, new BufferedWriter(new FileWriter(jobFile)));
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 通过id删除职业
     *
     * @param jobId 职业id
     * @return 是否成功
     */
    public boolean deleteJob(long jobId) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("id", jobId);
                DbUtil.builder(JobsCraftTableNames.JOB.getName(), getDataSource()).delete(entity);
                return true;
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return false;
                }
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection jobSection = configuration.getConfigurationSection("job");
                    if (jobSection == null) {
                        return false;
                    }
                    long id = jobSection.getLong("id");
                    if (id == jobId) {
                        return file.delete();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 通过名称删除职业
     *
     * @param name 职业名称
     * @return 是否成功
     */
    public boolean deleteJob(String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                DbUtil.builder(JobsCraftTableNames.JOB.getName(), getDataSource()).delete(entity);
                return true;
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return false;
                }
                return file.delete();
            }
        }
        return false;
    }

    /**
     * 清空所有职业
     */
    public boolean clearJobs() {
        switch (getType()) {
            case DATABASE -> {
                DbUtil.builder(JobsCraftTableNames.JOB.getName(), getDataSource()).execute("truncate table " + JobsCraftTableNames.JOB.getName() + ";");
                return true;
            }
            case LOCAL -> {
                //return FileUtil.clean(getFolder());
            }
        }
        return false;
    }
}
