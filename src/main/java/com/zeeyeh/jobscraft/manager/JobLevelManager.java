package com.zeeyeh.jobscraft.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.zeeyeh.devtoolkit.database.DBEntity;
import com.zeeyeh.jobscraft.JobsCraft;
import com.zeeyeh.jobscraft.database.SimpleDatabase;
import com.zeeyeh.jobscraft.entity.DataSaveType;
import com.zeeyeh.jobscraft.entity.JobLevel;
import com.zeeyeh.jobscraft.entity.JobsCraftTableNames;
import com.zeeyeh.jobscraft.factory.JobLevelFactory;
import com.zeeyeh.jobscraft.utils.DbUtil;
import com.zeeyeh.jobscraft.utils.JsonUtil;
import com.zeeyeh.jobscraft.utils.MapUtil;
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

public class JobLevelManager extends CraftManager {
    private List<JobLevel> cacheJobLevels;

    public JobLevelManager() {
        cacheJobLevels = new ArrayList<>();
    }

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
            setFolder(new File(JobsCraft.getInstance().getDataFolder(), "levels"));
            if (!getFolder().exists()) {
                getFolder().mkdirs();
            }
        }
        this.cacheJobLevels = getJobLevels();
    }

    /**
     * 获取所有等级
     */
    public List<JobLevel> getJobLevels() {
        List<JobLevel> list = new ArrayList<>();
        switch (getType()) {
            case DATABASE -> {
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_LEVEL.getName(), getDataSource()).findAll();
                for (DBEntity entity : entities) {
                    Long id = entity.getLong("id");
                    String name = entity.getString("name");
                    String title = entity.getString("title");
                    Long maxExp = entity.getLong("maxExp");
                    Long nextId = entity.getLong("nextId");
                    JsonArray playerArrays = JsonUtil.toJsonArray(entity.getString("players"));
                    List<String> players = new ArrayList<>();
                    for (JsonElement playerArray : playerArrays) {
                        players.add(playerArray.getAsString());
                    }
                    list.add(new JobLevel(id, name, title, maxExp, nextId, players));
                }
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return Collections.emptyList();
                }
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection levelSection = configuration.getConfigurationSection("level");
                    if (levelSection == null) {
                        continue;
                    }
                    long id = levelSection.getLong("id");
                    String name = levelSection.getString("name");
                    String title = levelSection.getString("title");
                    long maxExp = levelSection.getLong("maxExp");
                    long nextId = levelSection.getLong("nextId");
                    List<String> players = levelSection.getStringList("players");
                    list.add(new JobLevel(id, name, title, maxExp, nextId, players));
                }
            }
        }
        return list;
    }

    /**
     * 根据Id获取职业等级信息
     *
     * @param levelId 等级Id
     */
    public JobLevel getJobLevel(long levelId) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("id", levelId);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_LEVEL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                JsonArray playerArrays = JsonUtil.toJsonArray(searchEntity.getString("players"));
                List<String> players = new ArrayList<>();
                for (JsonElement jsonElement : playerArrays) {
                    players.add(jsonElement.getAsString());
                }
                return new JobLevel(
                        searchEntity.getLong("id"),
                        searchEntity.getString("name"),
                        searchEntity.getString("title"),
                        searchEntity.getLong("maxExp"),
                        searchEntity.getLong("nextId"),
                        players);
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return null;
                }
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection levelSection = configuration.getConfigurationSection("level");
                    if (levelSection == null) {
                        return null;
                    }
                    long id = levelSection.getLong("id");
                    if (id == levelId) {
                        String name = levelSection.getString("name");
                        String title = levelSection.getString("title");
                        long maxExp = levelSection.getLong("maxExp");
                        long nextId = levelSection.getLong("nextId");
                        List<String> players = levelSection.getStringList("players");
                        return new JobLevel(id, name, title, maxExp, nextId, players);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据名称获取职业等级信息
     *
     * @param levelName 等级名称
     */
    public JobLevel getJobLevel(String levelName) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", levelName);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_LEVEL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                JsonArray playerArrays = JsonUtil.toJsonArray(searchEntity.getString("players"));
                List<String> players = new ArrayList<>();
                for (JsonElement jsonElement : playerArrays) {
                    players.add(jsonElement.getAsString());
                }
                return new JobLevel(
                        searchEntity.getLong("id"),
                        searchEntity.getString("name"),
                        searchEntity.getString("title"),
                        searchEntity.getLong("maxExp"),
                        searchEntity.getLong("nextId"),
                        players);
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return null;
                }
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection levelSection = configuration.getConfigurationSection("level");
                    if (levelSection == null) {
                        return null;
                    }
                    String name = levelSection.getString("name");
                    if (name == null) {
                        return null;
                    }
                    if (name.equals(levelName)) {
                        long id = levelSection.getLong("id");
                        String title = levelSection.getString("title");
                        long maxExp = levelSection.getLong("maxExp");
                        long nextId = levelSection.getLong("nextId");
                        List<String> players = levelSection.getStringList("players");
                        return new JobLevel(id, name, title, maxExp, nextId, players);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据玩家对象获取职业等级
     *
     * @param player 玩家对象
     */
    public JobLevel getJobLevelByPlayer(Player player) {
        return getJobLevelByPlayer(player.getName());
    }

    /**
     * 根据玩家名称获取职业等级
     *
     * @param playerName 玩家名称
     */
    public JobLevel getJobLevelByPlayer(String playerName) {
        switch (getType()) {
            case DATABASE -> {
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_LEVEL.getName(), getDataSource()).findAll();
                if (entities.size() == 0) {
                    return null;
                }
                for (DBEntity searchEntity : entities) {
                    JsonArray playerArrays = JsonUtil.toJsonArray(searchEntity.getString("players"));
                    List<String> players = new ArrayList<>();
                    for (JsonElement jsonElement : playerArrays) {
                        players.add(jsonElement.getAsString());
                    }
                    boolean isExists = false;
                    for (String player : players) {
                        if (getPlayerName(player).equals(playerName)) {
                            isExists = true;
                            break;
                        }
                    }
                    if (isExists) {
                        return new JobLevel(
                                searchEntity.getLong("id"),
                                searchEntity.getString("name"),
                                searchEntity.getString("title"),
                                searchEntity.getLong("maxExp"),
                                searchEntity.getLong("nextId"),
                                players);
                    }
                }
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return null;
                }
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection levelSection = configuration.getConfigurationSection("level");
                    if (levelSection == null) {
                        return null;
                    }
                    List<String> players = levelSection.getStringList("players");
                    boolean isExist = false;
                    for (String playerLine : players) {
                        if (getPlayerName(playerLine).equals(playerName)) {
                            isExist = true;
                            break;
                        }
                    }
                    if (isExist) {
                        String name = levelSection.getString("name");
                        long id = levelSection.getLong("id");
                        String title = levelSection.getString("title");
                        long maxExp = levelSection.getLong("maxExp");
                        long nextId = levelSection.getLong("nextId");
                        return new JobLevel(id, name, title, maxExp, nextId, players);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 从玩家信息行中提取玩家名称
     *
     * @param playerLine 玩家信息行内容
     */
    public String getPlayerName(String playerLine) {
        return playerLine.substring(0, playerLine.indexOf(":"));
    }

    /**
     * 从玩家信息行中提取职业名称
     *
     * @param playerLine 玩家信息行
     */
    public long getPlayerJobId(String playerLine) {
        return Long.parseLong(playerLine.substring(playerLine.indexOf(":") + 1, playerLine.lastIndexOf(":")));
    }

    /**
     * 从玩家信息行中提示职业经验值
     *
     * @param playerLine 玩家信息行
     */
    public String getPlayerExp(String playerLine) {
        return playerLine.substring(playerLine.lastIndexOf(":") + 1);
    }

    /**
     * 向等级中添加玩家职业信息
     *
     * @param playerName 玩家名称
     * @param jobId      职业等级
     * @param levelName  等级名称
     */
    public boolean addPlayer(String playerName, long jobId, String levelName) {
        switch (getType()) {
            case DATABASE -> {
                JobLevel jobLevel = getJobLevel(levelName);
                if (jobLevel == null) {
                    return false;
                }
                List<String> players = jobLevel.getPlayers();
                boolean isExists = false;
                for (String playerLine : players) {
                    String player = getPlayerName(playerLine);
                    long playerJobId = getPlayerJobId(playerLine);
                    if (player.equals(playerName) && jobId == playerJobId) {
                        isExists = true;
                        break;
                    }
                }
                if (isExists) {
                    return false;
                }
                players.add(playerName + ":" + jobId + ":0");
                DBEntity entity = new DBEntity();
                entity.set("players", JsonUtil.toJsonArrayString(players));
                DBEntity where = new DBEntity();
                where.set("name", jobLevel.getName());
                DbUtil.builder(JobsCraftTableNames.JOB_LEVEL.getName(), getDataSource()).update(entity, where);
                return true;
            }
            case LOCAL -> {
                File file = new File(getFolder(), levelName + ".yml");
                if (!file.exists()) {
                    return false;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection levelSection = configuration.getConfigurationSection("level");
                if (levelSection == null) {
                    return false;
                }
                List<String> players = levelSection.getStringList("players");
                boolean isExists = false;
                for (String playerLine : players) {
                    String player = getPlayerName(playerLine);
                    long playerJobId = getPlayerJobId(playerLine);
                    if (player.equals(playerName) && jobId == playerJobId) {
                        isExists = true;
                        break;
                    }
                }
                if (isExists) {
                    return false;
                }
                players.add(playerName + ":" + jobId + ":0");
                levelSection.set("players", players);
                configuration.set("level", levelSection);
                try {
                    configuration.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 向等级中删除玩家职业信息
     *
     * @param playerName 玩家名称
     * @param jobId      职业等级
     * @param levelName  等级名称
     */
    public boolean removePlayer(String playerName, long jobId, String levelName) {
        switch (getType()) {
            case DATABASE -> {
                JobLevel jobLevel = getJobLevel(levelName);
                if (jobLevel == null) {
                    return false;
                }
                List<String> players = jobLevel.getPlayers();
                int i = 0;
                for (int j = 0; j < players.size(); j++) {
                    String player = getPlayerName(players.get(j));
                    long playerJobId = getPlayerJobId(players.get(j));
                    if (player.equals(playerName) && jobId == playerJobId) {
                        players.remove(i);
                    }
                }
                //for (String playerLine : players) {
                //    if (playerLine.isEmpty()) {
                //        continue;
                //    }
                //    String player = getPlayerName(playerLine);
                //    long playerJobId = getPlayerJobId(playerLine);
                //    if (player.equals(playerName) && jobId == playerJobId) {
                //        players.remove(i);
                //    }
                //    i++;
                //}
                DBEntity entity = new DBEntity();
                entity.set("players", JsonUtil.toJsonArrayString(players));
                DBEntity where = new DBEntity();
                where.set("name", jobLevel.getName());
                int updated = DbUtil.builder(JobsCraftTableNames.JOB_LEVEL.getName(), getDataSource()).update(entity, where);
                return true;
            }
            case LOCAL -> {
                File file = new File(getFolder(), levelName + ".yml");
                if (!file.exists()) {
                    return false;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection levelSection = configuration.getConfigurationSection("level");
                if (levelSection == null) {
                    return false;
                }
                List<String> players = levelSection.getStringList("players");
                int i = 0;
                for (String playerLine : players) {
                    String player = getPlayerName(playerLine);
                    long playerJobId = getPlayerJobId(playerLine);
                    if (player.equals(playerName) && jobId == playerJobId) {
                        players.remove(i);
                    }
                    i++;
                }
                levelSection.set("players", players);
                configuration.set("level", levelSection);
                try {
                    configuration.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 增加玩家指定职业的经验值
     *
     * @param playerName 玩家名称
     * @param jobId      职业id
     * @param exp        目标经验值
     */
    public boolean giveExp(String playerName, long jobId, int exp) {
        switch (getType()) {
            case DATABASE -> {
                JobLevel jobLevelByPlayer = getJobLevelByPlayer(playerName);
                if (jobLevelByPlayer == null) {
                    return false;
                }
                long maxExp = jobLevelByPlayer.getMaxExp();
                List<String> players = jobLevelByPlayer.getPlayers();
                int i = 0;
                for (String playerLine : players) {
                    String player = getPlayerName(playerLine);
                    String playerExp = getPlayerExp(playerLine);
                    long playerJobId = getPlayerJobId(playerLine);
                    if (player.equals(playerName) && playerJobId == jobId) {
                        int targetExp = Integer.parseInt(playerExp) + exp;
                        if (targetExp >= maxExp) {
                            long nextId = jobLevelByPlayer.getNextId();
                            if (nextId > 0) {
                                JobLevel jobLevel = getJobLevel(nextId);
                                if (jobLevel == null) {
                                    return false;
                                }
                                removePlayer(playerName, jobId, jobLevelByPlayer.getName());
                                addPlayer(playerName, playerJobId, jobLevel.getName());
                                return true;
                            }
                        } else {
                            players.remove(i);
                            players.add(playerName + ":" + playerJobId + ":" + targetExp);
                        }
                    }
                    i++;
                }
                DBEntity entity = new DBEntity();
                entity.set("players", JsonUtil.toJsonArrayString(players));
                DBEntity where = new DBEntity();
                where.set("name", jobLevelByPlayer.getName());
                DbUtil.builder(JobsCraftTableNames.JOB_LEVEL.getName(), getDataSource()).update(entity, where);
                return true;
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return false;
                }
                int i = 0;
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection levelSection = configuration.getConfigurationSection("level");
                    if (levelSection == null) {
                        return false;
                    }
                    List<String> levelPlayers = levelSection.getStringList("players");
                    String name = levelSection.getString("name");
                    long maxExp = levelSection.getLong("maxExp");
                    long nextId = levelSection.getLong("nextId");
                    for (String levelPlayer : levelPlayers) {
                        String player = getPlayerName(levelPlayer);
                        String playerExp = getPlayerExp(levelPlayer);
                        long playerJobId = getPlayerJobId(levelPlayer);
                        if (playerName.equals(player) && playerJobId == jobId) {
                            int targetExp = Integer.parseInt(playerExp) + exp;
                            if (targetExp >= maxExp) {
                                // 升级
                                removePlayer(playerName, playerJobId, name);
                                JobLevel jobLevel = getJobLevel(nextId);
                                addPlayer(playerName, playerJobId, jobLevel.getName());
                                return true;
                            } else {
                                levelPlayers.remove(i);
                                levelPlayers.add(playerName + ":" + playerJobId + ":" + targetExp);
                            }
                        }
                    }
                    levelSection.set("players", levelPlayers);
                    configuration.set("level", levelSection);
                    try {
                        configuration.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                    i++;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 增加玩家指定职业的经验值
     *
     * @param playerName 玩家名称
     * @param jobId      职业id
     * @param exp        目标经验值
     */
    public boolean takeExp(String playerName, long jobId, int exp) {
        switch (getType()) {
            case DATABASE -> {
                JobLevel jobLevelByPlayer = getJobLevelByPlayer(playerName);
                List<String> players = jobLevelByPlayer.getPlayers();
                int i = 0;
                for (String playerLine : players) {
                    String player = getPlayerName(playerLine);
                    String playerExp = getPlayerExp(playerLine);
                    long playerJobId = getPlayerJobId(playerLine);
                    if (player.equals(playerName) && playerJobId == jobId) {
                        int haveExp = Integer.parseInt(playerExp);
                        int targetExp = haveExp - exp;
                        if (targetExp <= haveExp) {
                            players.remove(i);
                            players.add(playerName + ":" + playerJobId + ":" + targetExp);
                        } else {
                            return false;
                        }
                    }
                    i++;
                }
                DBEntity entity = new DBEntity();
                entity.set("players", JsonUtil.toJsonArrayString(players));
                DBEntity where = new DBEntity();
                where.set("name", jobLevelByPlayer.getName());
                DbUtil.builder(JobsCraftTableNames.JOB_LEVEL.getName(), getDataSource()).update(entity, where);
                return true;
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return false;
                }
                int i = 0;
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection levelSection = configuration.getConfigurationSection("level");
                    if (levelSection == null) {
                        return false;
                    }
                    List<String> levelPlayers = levelSection.getStringList("players");
                    long maxExp = levelSection.getLong("maxExp");
                    for (String levelPlayer : levelPlayers) {
                        String player = getPlayerName(levelPlayer);
                        String playerExp = getPlayerExp(levelPlayer);
                        long playerJobId = getPlayerJobId(levelPlayer);
                        if (playerName.equals(player) && playerJobId == jobId) {
                            int haveExp = Integer.parseInt(playerExp);
                            int targetExp = haveExp - exp;
                            if (targetExp > maxExp) {
                                levelPlayers.remove(i);
                                levelPlayers.add(playerName + ":" + playerJobId + ":" + targetExp);
                            } else {
                                return false;
                            }
                        }
                    }
                    levelSection.set("players", levelPlayers);
                    configuration.set("level", levelSection);
                    try {
                        configuration.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                    i++;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 创建一个职业等级
     *
     * @param name   等级名称
     * @param title  等级标题
     * @param maxExp 最高经验值
     */
    public boolean createJobLevel(String name, String title, long maxExp) {
        JobLevel jobLevel = JobLevelFactory.createJobLevel(name, title, maxExp, -1000);
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("id", jobLevel.getId());
                entity.set("name", jobLevel.getName());
                entity.set("title", jobLevel.getTitle());
                entity.set("maxExp", jobLevel.getMaxExp());
                entity.set("nextId", jobLevel.getNextId());
                entity.set("players", JsonUtil.toJsonArrayString(jobLevel.getPlayers()));
                DbUtil.builder(JobsCraftTableNames.JOB_LEVEL.getName(), getDataSource()).insert(entity);
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (file.exists()) {
                    return false;
                }
                Yaml yaml = new Yaml();
                Map<String, Object> map = MapUtil.asMap("level", jobLevel);
                try {
                    yaml.dump(map, new BufferedWriter(new FileWriter(file)));
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 删除一个职业等级
     *
     * @param levelId 等级Id
     */
    public boolean deleteJobLevel(long levelId) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("id", levelId);
                DbUtil.builder(JobsCraftTableNames.JOB_LEVEL.getName(), getDataSource()).delete(entity);
                return true;
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return false;
                }
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection levelSection = configuration.getConfigurationSection("level");
                    if (levelSection == null) {
                        return false;
                    }
                    long id = levelSection.getLong("id");
                    if (id == levelId) {
                        return file.delete();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 删除一个职业等级
     *
     * @param name 等级名称
     */
    public boolean deleteJobLevel(String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                DbUtil.builder(JobsCraftTableNames.JOB_LEVEL.getName(), getDataSource()).delete(entity);
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
     * 清空所有职业等级
     */
    public boolean clearJobLevels() {
        switch (getType()) {
            case DATABASE -> {
                DbUtil.builder(JobsCraftTableNames.JOB_LEVEL.getName(), getDataSource()).execute("truncate table " + JobsCraftTableNames.JOB_LEVEL.getName() + ";");
                return true;
            }
            case LOCAL -> {
                //Files.delete();
                //return FileUtil.clean(getFolder());
            }
        }
        return false;
    }
}
