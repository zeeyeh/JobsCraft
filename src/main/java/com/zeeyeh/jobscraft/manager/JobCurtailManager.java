package com.zeeyeh.jobscraft.manager;

import com.zeeyeh.devtoolkit.database.DBEntity;
import com.zeeyeh.jobscraft.JobsCraft;
import com.zeeyeh.jobscraft.database.SimpleDatabase;
import com.zeeyeh.jobscraft.entity.DataSaveType;
import com.zeeyeh.jobscraft.entity.JobCurtail;
import com.zeeyeh.jobscraft.entity.JobsCraftTableNames;
import com.zeeyeh.jobscraft.utils.DbUtil;
import com.zeeyeh.jobscraft.utils.JsonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JobCurtailManager extends CraftManager {
    private List<JobCurtail> cacheJobCurtail;

    public JobCurtailManager() {
        this.cacheJobCurtail = new ArrayList<JobCurtail>();
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
            setFolder(new File(JobsCraft.getInstance().getDataFolder(), "curtails"));
            if (!getFolder().exists()) {
                getFolder().mkdirs();
            }
        }
        this.cacheJobCurtail = getJobCurtails();
    }

    public List<JobCurtail> getJobCurtails() {
        List<JobCurtail> list = new ArrayList<>();
        switch (getType()) {
            case DATABASE -> {
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).findAll();
                for (DBEntity entity : entities) {
                    list.add(toJobCurtail(entity));
                }
                //List<DBEntity> entities;
                //try {
                //    entities = Db.use(getDataSource()).findAll(JobsCraftTableNames.JOB_CURTAIL.getName());
                //    for (DBEntity entity : entities) {
                //        list.add(toJobCurtail(entity));
                //    }
                //} catch (SQLException e) {
                //    e.printStackTrace();
                //    return Collections.emptyList();
                //}
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return Collections.emptyList();
                }
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                    if (curtailSection == null) {
                        continue;
                    }
                    long id = curtailSection.getLong("id");
                    String name = curtailSection.getString("name");
                    long jobId = curtailSection.getLong("jobId");
                    long levelId = curtailSection.getLong("levelId");
                    List<String> tools = curtailSection.getStringList("tools");
                    List<String> foods = curtailSection.getStringList("foods");
                    List<String> destructs = curtailSection.getStringList("destructs");
                    List<String> places = curtailSection.getStringList("places");
                    List<String> recipes = curtailSection.getStringList("recipes");
                    List<String> interacts = curtailSection.getStringList("interacts");
                    List<String> attacks = curtailSection.getStringList("attacks");
                    List<String> buffs = curtailSection.getStringList("buffs");
                    list.add(new JobCurtail(id, name, jobId, levelId, tools, foods, destructs, places, recipes, interacts, attacks, buffs));
                }
            }
        }
        return list;
    }

    public JobCurtail toJobCurtail(DBEntity entity) {
        Long id = entity.getLong("id");
        String name = entity.getString("name");
        Long jobId = entity.getLong("jobId");
        Long levelId = entity.getLong("levelId");
        String toolsString = entity.getString("tools");
        String foodsString = entity.getString("foods");
        String destructsString = entity.getString("destructs");
        String placesString = entity.getString("places");
        String recipesString = entity.getString("recipes");
        String interactsString = entity.getString("interacts");
        String attacksString = entity.getString("attacks");
        String buffsString = entity.getString("buffs");
        List<String> tools = JsonUtil.toListString(JsonUtil.toJsonArray(toolsString));
        List<String> foods = JsonUtil.toListString(JsonUtil.toJsonArray(foodsString));
        List<String> destructs = JsonUtil.toListString(JsonUtil.toJsonArray(destructsString));
        List<String> places = JsonUtil.toListString(JsonUtil.toJsonArray(placesString));
        List<String> recipes = JsonUtil.toListString(JsonUtil.toJsonArray(recipesString));
        List<String> interacts = JsonUtil.toListString(JsonUtil.toJsonArray(interactsString));
        List<String> attacks = JsonUtil.toListString(JsonUtil.toJsonArray(attacksString));
        List<String> buffs = JsonUtil.toListString(JsonUtil.toJsonArray(buffsString));
        return new JobCurtail(id, name, jobId, levelId, tools, foods, destructs, places, recipes, interacts, attacks, buffs);
    }

    public JobCurtail getJobCurtail(long jobId, long levelId) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("jobId", jobId);
                entity.set("levelId", levelId);
                //DBEntity entity = .create(JobsCraftTableNames.JOB_CURTAIL.getName()).set("jobId", jobId).set("levelId", levelId);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                return toJobCurtail(searchEntity);
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return null;
                }
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                    if (curtailSection == null) {
                        continue;
                    }
                    long curtailJobId = curtailSection.getLong("jobId");
                    long curtailLevelId = curtailSection.getLong("levelId");
                    if (curtailJobId == jobId && curtailLevelId == levelId) {
                        String name = curtailSection.getString("name");
                        long curtailId = curtailSection.getLong("id");
                        List<String> tools = curtailSection.getStringList("tools");
                        List<String> foods = curtailSection.getStringList("foods");
                        List<String> destructs = curtailSection.getStringList("destructs");
                        List<String> places = curtailSection.getStringList("places");
                        List<String> recipes = curtailSection.getStringList("recipes");
                        List<String> interacts = curtailSection.getStringList("interacts");
                        List<String> attacks = curtailSection.getStringList("attacks");
                        List<String> buffs = curtailSection.getStringList("buffs");
                        return new JobCurtail(curtailId, name, jobId, levelId, tools, foods, destructs, places, recipes, interacts, attacks, buffs);
                    }
                }
            }
        }
        return null;
    }

    public JobCurtail getJobCurtail(long id) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("id", id);
                //= DBEntity.create(JobsCraftTableNames.JOB_CURTAIL.getName()).
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                return toJobCurtail(searchEntity);
            }
            case LOCAL -> {
                File[] files = getFolder().listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".yml"));
                if (files == null) {
                    return null;
                }
                for (File file : files) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                    ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                    if (curtailSection == null) {
                        continue;
                    }
                    long curtailId = curtailSection.getLong("id");
                    if (curtailId == id) {
                        String name = curtailSection.getString("name");
                        long jobId = curtailSection.getLong("jobId");
                        long levelId = curtailSection.getLong("levelId");
                        List<String> tools = curtailSection.getStringList("tools");
                        List<String> foods = curtailSection.getStringList("foods");
                        List<String> destructs = curtailSection.getStringList("destructs");
                        List<String> places = curtailSection.getStringList("places");
                        List<String> recipes = curtailSection.getStringList("recipes");
                        List<String> interacts = curtailSection.getStringList("interacts");
                        List<String> attacks = curtailSection.getStringList("attacks");
                        List<String> buffs = curtailSection.getStringList("buffs");
                        return new JobCurtail(curtailId, name, jobId, levelId, tools, foods, destructs, places, recipes, interacts, attacks, buffs);
                    }
                }
            }
        }
        return null;
    }

    public JobCurtail getJobCurtail(String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                return toJobCurtail(searchEntity);
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    long id = curtailSection.getLong("id");
                    long jobId = curtailSection.getLong("jobId");
                    long levelId = curtailSection.getLong("levelId");
                    List<String> tools = curtailSection.getStringList("tools");
                    List<String> foods = curtailSection.getStringList("foods");
                    List<String> destructs = curtailSection.getStringList("destructs");
                    List<String> places = curtailSection.getStringList("places");
                    List<String> recipes = curtailSection.getStringList("recipes");
                    List<String> interacts = curtailSection.getStringList("interacts");
                    List<String> attacks = curtailSection.getStringList("attacks");
                    List<String> buffs = curtailSection.getStringList("buffs");
                    return new JobCurtail(id, curtailName, jobId, levelId, tools, foods, destructs, places, recipes, interacts, attacks, buffs);
                }
            }
        }
        return null;
    }

    public List<String> getJobCurtailTools(String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                String toolsString = searchEntity.getString("tools");
                return JsonUtil.toListString(JsonUtil.toJsonArray(toolsString));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    return curtailSection.getStringList("tools");
                }
            }
        }
        return null;
    }

    public List<String> getJobCurtailFoods(String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                String toolsString = searchEntity.getString("foods");
                return JsonUtil.toListString(JsonUtil.toJsonArray(toolsString));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    return curtailSection.getStringList("foods");
                }
            }
        }
        return null;
    }

    public List<String> getJobCurtailDestructs(String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                String toolsString = searchEntity.getString("destructs");
                return JsonUtil.toListString(JsonUtil.toJsonArray(toolsString));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    return curtailSection.getStringList("destructs");
                }
            }
        }
        return null;
    }

    public List<String> getJobCurtailPlaces(String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                String toolsString = searchEntity.getString("places");
                return JsonUtil.toListString(JsonUtil.toJsonArray(toolsString));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    return curtailSection.getStringList("places");
                }
            }
        }
        return null;
    }

    public List<String> getJobCurtailRecipes(String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                String toolsString = searchEntity.getString("recipes");
                return JsonUtil.toListString(JsonUtil.toJsonArray(toolsString));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    return curtailSection.getStringList("recipes");
                }
            }
        }
        return null;
    }

    public List<String> getJobCurtailInteracts(String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                String toolsString = searchEntity.getString("interacts");
                return JsonUtil.toListString(JsonUtil.toJsonArray(toolsString));

            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    return curtailSection.getStringList("interacts");
                }
            }
        }
        return null;
    }

    public List<String> getJobCurtailAttacks(String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                String toolsString = searchEntity.getString("attacks");
                return JsonUtil.toListString(JsonUtil.toJsonArray(toolsString));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    return curtailSection.getStringList("attacks");
                }
            }
        }
        return null;
    }

    public List<String> getJobCurtailBuffs(String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                DBEntity searchEntity = entities.get(0);
                String toolsString = searchEntity.getString("buffs");
                return JsonUtil.toListString(JsonUtil.toJsonArray(toolsString));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    return curtailSection.getStringList("buffs");
                }
            }
        }
        return null;
    }

    public JobCurtail setJobCurtailTools(String name, List<String> tools) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity where = new DBEntity();
                where.set("name", name);
                DBEntity entity = new DBEntity();
                entity.set("tools", tools);
                DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).update(entity, where);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                return toJobCurtail(entities.get(0));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    curtailSection.set("tools", tools);
                    configuration.set("curtail", curtailSection);
                    try {
                        configuration.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public JobCurtail setJobCurtailFoods(String name, List<String> foods) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity where = new DBEntity();
                where.set("name", name);
                DBEntity entity = new DBEntity();
                entity.set("foods", foods);
                DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).update(entity, where);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                return toJobCurtail(entities.get(0));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    curtailSection.set("foods", foods);
                    configuration.set("curtail", curtailSection);
                    try {
                        configuration.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public JobCurtail setJobCurtailDestructs(String name, List<String> destructs) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity where = new DBEntity();
                where.set("name", name);
                DBEntity entity = new DBEntity();
                entity.set("destructs", destructs);
                DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).update(entity, where);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                return toJobCurtail(entities.get(0));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    curtailSection.set("destructs", destructs);
                    configuration.set("curtail", curtailSection);
                    try {
                        configuration.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public JobCurtail setJobCurtailPlaces(String name, List<String> places) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity where = new DBEntity();
                where.set("name", name);
                DBEntity entity = new DBEntity();
                entity.set("places", places);
                DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).update(entity, where);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                return toJobCurtail(entities.get(0));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    curtailSection.set("places", places);
                    configuration.set("curtail", curtailSection);
                    try {
                        configuration.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public JobCurtail setJobCurtailRecipes(String name, List<String> recipes) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity where = new DBEntity();
                where.set("name", name);
                DBEntity entity = new DBEntity();
                entity.set("recipes", recipes);
                DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).update(entity, where);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                return toJobCurtail(entities.get(0));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    curtailSection.set("recipes", recipes);
                    configuration.set("curtail", curtailSection);
                    try {
                        configuration.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public JobCurtail setJobCurtailInteracts(String name, List<String> interacts) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity where = new DBEntity();
                where.set("name", name);
                DBEntity entity = new DBEntity();
                entity.set("interacts", interacts);
                DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).update(entity, where);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                return toJobCurtail(entities.get(0));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    curtailSection.set("interacts", interacts);
                    configuration.set("curtail", curtailSection);
                    try {
                        configuration.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public JobCurtail setJobCurtailAttacks(String name, List<String> attacks) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity where = new DBEntity();
                where.set("name", name);
                DBEntity entity = new DBEntity();
                entity.set("attacks", attacks);
                DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).update(entity, where);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                return toJobCurtail(entities.get(0));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    curtailSection.set("attacks", attacks);
                    configuration.set("curtail", curtailSection);
                    try {
                        configuration.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public JobCurtail setJobCurtailBuffs(String name, List<String> buffs) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity where = new DBEntity();
                where.set("name", name);
                DBEntity entity = new DBEntity();
                entity.set("buffs", buffs);
                DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).update(entity, where);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return null;
                }
                return toJobCurtail(entities.get(0));
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return null;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return null;
                }
                String curtailName = curtailSection.getString("name");
                if (curtailName == null) {
                    return null;
                }
                if (curtailName.equals(name)) {
                    curtailSection.set("buffs", buffs);
                    configuration.set("curtail", curtailSection);
                    try {
                        configuration.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public boolean hasJobCurtailTool(String tool, List<String> tools) {
        for (String item : tools) {
            if (item.startsWith(tool)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasJobCurtailFood(String food, List<String> foods) {
        for (String item : foods) {
            if (item.startsWith(food)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasJobCurtailDestruct(String destruct, List<String> destructs) {
        for (String item : destructs) {
            if (item.startsWith(destruct)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasJobCurtailPlace(String place, List<String> places) {
        for (String item : places) {
            if (item.startsWith(place)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasJobCurtailRecipe(String recipe, List<String> recipes) {
        for (String item : recipes) {
            if (item.startsWith(recipe)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasJobCurtailInteract(String interact, List<String> interacts) {
        for (String item : interacts) {
            if (item.startsWith(interact)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasJobCurtailAttack(String attack, List<String> attacks) {
        for (String item : attacks) {
            if (item.startsWith(attack)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasJobCurtailBuff(String buff, List<String> buffs) {
        for (String item : buffs) {
            if (item.startsWith(buff)) {
                return true;
            }
        }
        return false;
    }

    public List<String> addJobCurtailTool(String tool, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> oldTools = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("tools")));
                if (hasJobCurtailTool(tool, oldTools)) {
                    return oldTools;
                }
                oldTools.add(tool);
                JobCurtail jobCurtail = setJobCurtailTools(name, oldTools);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> tools = curtailSection.getStringList("tools");
                if (hasJobCurtailTool(tool, tools)) {
                    return Collections.emptyList();
                }
                tools.add(tool);
                JobCurtail jobCurtail = setJobCurtailTools(name, tools);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
        }
        return Collections.emptyList();
    }

    public List<String> addJobCurtailFood(String food, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> foods = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("foods")));
                if (hasJobCurtailFood(food, foods)) {
                    return foods;
                }
                foods.add(food);
                JobCurtail jobCurtail = setJobCurtailTools(name, foods);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> foods = curtailSection.getStringList("foods");
                if (hasJobCurtailFood(food, foods)) {
                    return Collections.emptyList();
                }
                foods.add(food);
                JobCurtail jobCurtail = setJobCurtailTools(name, foods);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
        }
        return Collections.emptyList();
    }

    public List<String> addJobCurtailDestruct(String destruct, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> destructs = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("destructs")));
                if (hasJobCurtailDestruct(destruct, destructs)) {
                    return destructs;
                }
                destructs.add(destruct);
                JobCurtail jobCurtail = setJobCurtailTools(name, destructs);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> destructs = curtailSection.getStringList("destructs");
                if (hasJobCurtailDestruct(destruct, destructs)) {
                    return Collections.emptyList();
                }
                destructs.add(destruct);
                JobCurtail jobCurtail = setJobCurtailTools(name, destructs);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
        }
        return Collections.emptyList();
    }

    public List<String> addJobCurtailPlace(String place, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> places = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("places")));
                if (hasJobCurtailTool(place, places)) {
                    return Collections.emptyList();
                }
                places.add(place);
                JobCurtail jobCurtail = setJobCurtailTools(name, places);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> places = curtailSection.getStringList("places");
                if (hasJobCurtailPlace(place, places)) {
                    return Collections.emptyList();
                }
                places.add(place);
                JobCurtail jobCurtail = setJobCurtailPlaces(name, places);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getPlaces();
            }
        }
        return Collections.emptyList();
    }

    public List<String> addJobCurtailRecipe(String recipe, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> recipes = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("recipes")));
                if (hasJobCurtailRecipe(recipe, recipes)) {
                    return Collections.emptyList();
                }
                recipes.add(recipe);
                JobCurtail jobCurtail = setJobCurtailRecipes(name, recipes);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getRecipes();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> recipes = curtailSection.getStringList("recipes");
                if (hasJobCurtailRecipe(recipe, recipes)) {
                    return Collections.emptyList();
                }
                recipes.add(recipe);
                JobCurtail jobCurtail = setJobCurtailRecipes(name, recipes);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getRecipes();
            }
        }
        return Collections.emptyList();
    }

    public List<String> addJobCurtailInteract(String interact, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> interacts = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("interacts")));
                if (hasJobCurtailInteract(interact, interacts)) {
                    return interacts;
                }
                interacts.add(interact);
                JobCurtail jobCurtail = setJobCurtailInteracts(name, interacts);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getInteracts();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> interacts = curtailSection.getStringList("interacts");
                if (hasJobCurtailInteract(interact, interacts)) {
                    return Collections.emptyList();
                }
                interacts.add(interact);
                JobCurtail jobCurtail = setJobCurtailInteracts(name, interacts);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getInteracts();
            }
        }
        return Collections.emptyList();
    }

    public List<String> addJobCurtailAttack(String attack, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> attacks = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("attacks")));
                if (hasJobCurtailAttack(attack, attacks)) {
                    return attacks;
                }
                attacks.add(attack);
                JobCurtail jobCurtail = setJobCurtailAttacks(name, attacks);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getAttacks();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> attacks = curtailSection.getStringList("attacks");
                if (hasJobCurtailAttack(attack, attacks)) {
                    return Collections.emptyList();
                }
                attacks.add(attack);
                JobCurtail jobCurtail = setJobCurtailAttacks(name, attacks);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getAttacks();
            }
        }
        return Collections.emptyList();
    }

    public List<String> addJobCurtailBuff(String buff, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> buffs = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("buffs")));
                if (hasJobCurtailBuff(buff, buffs)) {
                    return buffs;
                }
                buffs.add(buff);
                JobCurtail jobCurtail = setJobCurtailBuffs(name, buffs);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getBuffs();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> buffs = curtailSection.getStringList("buffs");
                if (hasJobCurtailBuff(buff, buffs)) {
                    return Collections.emptyList();
                }
                buffs.add(buff);
                JobCurtail jobCurtail = setJobCurtailBuffs(name, buffs);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getBuffs();
            }
        }
        return Collections.emptyList();
    }

    //----------------------------------------------------------------
    public List<String> removeJobCurtailTool(String tool, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> tools = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("tools")));
                if (hasJobCurtailTool(tool, tools)) {
                    return tools;
                }
                tools.remove(tool);
                JobCurtail jobCurtail = setJobCurtailTools(name, tools);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> tools = curtailSection.getStringList("tools");
                if (hasJobCurtailTool(tool, tools)) {
                    return Collections.emptyList();
                }
                tools.remove(tool);
                JobCurtail jobCurtail = setJobCurtailTools(name, tools);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
        }
        return Collections.emptyList();
    }

    public List<String> removeJobCurtailFood(String food, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> foods = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("foods")));
                if (hasJobCurtailFood(food, foods)) {
                    return foods;
                }
                foods.remove(food);
                JobCurtail jobCurtail = setJobCurtailTools(name, foods);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> foods = curtailSection.getStringList("foods");
                if (hasJobCurtailFood(food, foods)) {
                    return Collections.emptyList();
                }
                foods.remove(food);
                JobCurtail jobCurtail = setJobCurtailTools(name, foods);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
        }
        return Collections.emptyList();
    }

    public List<String> removeJobCurtailDestruct(String destruct, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> destructs = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("destructs")));
                if (hasJobCurtailDestruct(destruct, destructs)) {
                    return destructs;
                }
                destructs.remove(destruct);
                JobCurtail jobCurtail = setJobCurtailTools(name, destructs);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> destructs = curtailSection.getStringList("destructs");
                if (hasJobCurtailDestruct(destruct, destructs)) {
                    return Collections.emptyList();
                }
                destructs.remove(destruct);
                JobCurtail jobCurtail = setJobCurtailTools(name, destructs);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
        }
        return Collections.emptyList();
    }

    public List<String> removeJobCurtailPlace(String place, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> places = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("places")));
                if (hasJobCurtailTool(place, places)) {
                    return Collections.emptyList();
                }
                places.remove(place);
                JobCurtail jobCurtail = setJobCurtailTools(name, places);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getTools();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> places = curtailSection.getStringList("places");
                if (hasJobCurtailPlace(place, places)) {
                    return Collections.emptyList();
                }
                places.remove(place);
                JobCurtail jobCurtail = setJobCurtailPlaces(name, places);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getPlaces();
            }
        }
        return Collections.emptyList();
    }

    public List<String> removeJobCurtailRecipe(String recipe, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> recipes = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("recipes")));
                if (hasJobCurtailRecipe(recipe, recipes)) {
                    return Collections.emptyList();
                }
                recipes.remove(recipe);
                JobCurtail jobCurtail = setJobCurtailRecipes(name, recipes);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getRecipes();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> recipes = curtailSection.getStringList("recipes");
                if (hasJobCurtailRecipe(recipe, recipes)) {
                    return Collections.emptyList();
                }
                recipes.remove(recipe);
                JobCurtail jobCurtail = setJobCurtailRecipes(name, recipes);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getRecipes();
            }
        }
        return Collections.emptyList();
    }

    public List<String> removeJobCurtailInteract(String interact, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> interacts = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("interacts")));
                if (hasJobCurtailInteract(interact, interacts)) {
                    return interacts;
                }
                interacts.remove(interact);
                JobCurtail jobCurtail = setJobCurtailInteracts(name, interacts);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getInteracts();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> interacts = curtailSection.getStringList("interacts");
                if (hasJobCurtailInteract(interact, interacts)) {
                    return Collections.emptyList();
                }
                interacts.remove(interact);
                JobCurtail jobCurtail = setJobCurtailInteracts(name, interacts);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getInteracts();
            }
        }
        return Collections.emptyList();
    }

    public List<String> removeJobCurtailAttack(String attack, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> attacks = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("attacks")));
                if (hasJobCurtailAttack(attack, attacks)) {
                    return attacks;
                }
                attacks.remove(attack);
                JobCurtail jobCurtail = setJobCurtailAttacks(name, attacks);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getAttacks();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> attacks = curtailSection.getStringList("attacks");
                if (hasJobCurtailAttack(attack, attacks)) {
                    return Collections.emptyList();
                }
                attacks.remove(attack);
                JobCurtail jobCurtail = setJobCurtailAttacks(name, attacks);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getAttacks();
            }
        }
        return Collections.emptyList();
    }

    public List<String> removeJobCurtailBuff(String buff, String name) {
        switch (getType()) {
            case DATABASE -> {
                DBEntity entity = new DBEntity();
                entity.set("name", name);
                List<DBEntity> entities = DbUtil.builder(JobsCraftTableNames.JOB_CURTAIL.getName(), getDataSource()).find(entity);
                if (entities.size() == 0) {
                    return Collections.emptyList();
                }
                DBEntity searchEntity = entities.get(0);
                List<String> buffs = JsonUtil.toListString(JsonUtil.toJsonArray(searchEntity.getString("buffs")));
                if (hasJobCurtailBuff(buff, buffs)) {
                    return buffs;
                }
                buffs.remove(buff);
                JobCurtail jobCurtail = setJobCurtailBuffs(name, buffs);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getBuffs();
            }
            case LOCAL -> {
                File file = new File(getFolder(), name + ".yml");
                if (!file.exists()) {
                    return Collections.emptyList();
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection curtailSection = configuration.getConfigurationSection("curtail");
                if (curtailSection == null) {
                    return Collections.emptyList();
                }
                List<String> buffs = curtailSection.getStringList("buffs");
                if (hasJobCurtailBuff(buff, buffs)) {
                    return Collections.emptyList();
                }
                buffs.remove(buff);
                JobCurtail jobCurtail = setJobCurtailBuffs(name, buffs);
                if (jobCurtail == null) {
                    return Collections.emptyList();
                }
                return jobCurtail.getBuffs();
            }
        }
        return Collections.emptyList();
    }
}
