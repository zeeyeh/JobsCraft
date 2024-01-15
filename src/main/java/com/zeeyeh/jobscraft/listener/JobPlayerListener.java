package com.zeeyeh.jobscraft.listener;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.zeeyeh.devtoolkit.annotation.PluginListener;
import com.zeeyeh.devtoolkit.message.Messenger;
import com.zeeyeh.jobscraft.JobsCraft;
import com.zeeyeh.jobscraft.api.JobsCraftLangApi;
import com.zeeyeh.jobscraft.entity.Job;
import com.zeeyeh.jobscraft.entity.JobCurtail;
import com.zeeyeh.jobscraft.entity.JobLevel;
import com.zeeyeh.jobscraft.manager.JobCurtailManager;
import com.zeeyeh.jobscraft.manager.JobLevelManager;
import com.zeeyeh.jobscraft.manager.JobManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.serverct.ersha.attributeapi.api.ExAttributeAPI;
import org.serverct.ersha.attributeapi.attribute.data.AttributeData;

import java.util.ArrayList;
import java.util.List;

@PluginListener
public class JobPlayerListener implements Listener {
    /**
     * 玩家加入
     *
     * @param event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        initBuff(player, 0, true);
    }

    /**
     * 方块被破坏
     *
     * @param event
     */
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        Block block = event.getBlock();
        String localizedString = toLocalizedString(block.getType().getKey());
        JobCurtail jobCurtail = getJobCurtail(name);
        if (jobCurtail == null) {
            event.setCancelled(true);
            return;
        }
        List<String> destructs = jobCurtail.getDestructs();
        boolean hasPermission = player.hasPermission("JobsCraft.event.jobscraft.break");
        int getExps = 0;
        for (String destruct : destructs) {
            if (destruct.startsWith(localizedString)) {
                hasPermission = true;
                getExps = getLocalizedExp(destruct);
                break;
            }
        }
        if (!hasPermission) {
            event.setCancelled(true);
            return;
        }
        Job playerJob = getPlayerJob(name);
        String result = givePlayerExp(name, playerJob, getExps);
        if (result != null) {
            Messenger.send(player, result);
        }
    }

    /**
     * 放置方块
     *
     * @param event
     */
    //@EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String localizedString = toLocalizedString(block.getType().getKey());
        Job playerJob = getPlayerJob(player.getName());
        if (playerJob == null) {
            event.setCancelled(true);
            return;
        }
        JobCurtail jobCurtail = getJobCurtail(player.getName());
        if (jobCurtail == null) {
            event.setCancelled(true);
            return;
        }
        List<String> places = jobCurtail.getPlaces();
        boolean hasPermission = false;
        int localizedExp = 0;
        for (String place : places) {
            if (place.startsWith(localizedString)) {
                hasPermission = true;
                localizedExp = getLocalizedExp(place);
                break;
            }
        }
        if (hasPermission) {
            String result = givePlayerExp(player.getName(), playerJob, localizedExp);
            if (result != null) {
                Messenger.send(player, result);
            }
        } else {
            event.setCancelled(true);
        }
    }

    /**
     * 玩家点击实体
     *
     * @param event
     */
    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        Entity entity = event.getRightClicked();
        String localizedString = toLocalizedString(entity.getType().getKey());
        JobCurtail jobCurtail = getJobCurtail(localizedString);
        if (jobCurtail == null) {
            event.setCancelled(true);
            return;
        }
        List<String> interacts = jobCurtail.getInteracts();
        boolean hasPermission = player.hasPermission("JobsCraft.event.jobscraft.interact");
        int getExps = 0;
        for (String interact : interacts) {
            if (interact.startsWith(localizedString)) {
                hasPermission = true;
                getExps = getLocalizedExp(interact);
                break;
            }
        }
        if (!hasPermission) {
            event.setCancelled(true);
            return;
        }
        Job playerJob = getPlayerJob(name);
        String result = givePlayerExp(name, playerJob, getExps);
        if (result != null) {
            Messenger.send(player, result);
        }
    }

    /**
     * 玩家与对象或空气交互
     *
     * @param event
     */
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Material material = event.getMaterial();
        String localizedString = toLocalizedString(material.getKey());
        Job playerJob = getPlayerJob(player.getName());
        if (playerJob == null) {
            event.setCancelled(true);
            return;
        }
        JobCurtail jobCurtail = getJobCurtail(player.getName());
        if (jobCurtail == null) {
            event.setCancelled(true);
            return;
        }
        if (Action.RIGHT_CLICK_BLOCK == action) {
            if (event.getMaterial().isBlock()) {
                // 放置方块
                List<String> places = jobCurtail.getPlaces();
                boolean hasPermission = player.hasPermission("JobsCraft.event.jobscraft.place");
                int localizedExp = 0;
                for (String place : places) {
                    if (place.startsWith(localizedString)) {
                        hasPermission = true;
                        localizedExp = getLocalizedExp(place);
                        break;
                    }
                }
                if (hasPermission) {
                    String result = givePlayerExp(player.getName(), playerJob, localizedExp);
                    if (result != null) {
                        Messenger.send(player, result);
                    }
                } else {
                    event.setCancelled(true);
                }
                return;
            }
        }
        if (Action.LEFT_CLICK_BLOCK == action || Action.PHYSICAL == action) {
            return;
        }
        if (material.isAir()) {
            return;
        }
        List<String> saveTools = JobsCraft.getConfigManager().getDefaultConfig().getStringList("tools");
        if (!saveTools.contains(localizedString)) {
            return;
        }
        List<String> tools = jobCurtail.getTools();
        if (event.hasBlock()) {
            boolean hasPermission = player.hasPermission("JobsCraft.event.jobscraft.usetool");
            int localizedExp = 0;
            // 使用工具对方块
            for (String tool : tools) {
                Block clickedBlock = event.getClickedBlock();
                if (clickedBlock == null) {
                    event.setCancelled(true);
                    return;
                }
                if (tool.startsWith(localizedString)) {
                    hasPermission = true;
                    localizedExp = getLocalizedExp(tool);
                    break;
                }
            }
            if (hasPermission) {
                String result = givePlayerExp(player.getName(), playerJob, localizedExp);
                if (result != null) {
                    Messenger.send(player, result);
                }
                return;
            } else {
                event.setCancelled(true);
            }
        }
    }

    /**
     * 消耗完物品
     *
     * @param event
     */
    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Job playerJob = getPlayerJob(player.getName());
        if (playerJob == null) {
            event.setCancelled(true);
            return;
        }
        Material type = event.getItem().getType();
        String localizedString = toLocalizedString(type.getKey());
        if (type.isEdible()) {
            JobCurtail jobCurtail = getJobCurtail(player.getName());
            if (jobCurtail == null) {
                event.setCancelled(true);
                return;
            }
            List<String> foods = jobCurtail.getFoods();
            boolean hasPermission = player.hasPermission("JobsCraft.event.jobscraft.edible");
            int localizedExp = 0;
            // 食物
            for (String food : foods) {
                if (food.startsWith(localizedString)) {
                    hasPermission = true;
                    localizedExp = getLocalizedExp(food);
                    break;
                }
            }
            if (hasPermission) {
                String result = givePlayerExp(player.getName(), playerJob, localizedExp);
                if (result != null) {
                    Messenger.send(player, result);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    /**
     * 主手发生变更
     *
     * @param event
     */
    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        int newSlot = event.getNewSlot();
        initBuff(player, newSlot, false);
    }


    public void initBuff(Player player, int newSlot, boolean hasJoin) {
        ItemStack itemStack;
        if (hasJoin) {
            itemStack = player.getInventory().getItemInMainHand();
        } else {
            itemStack = player.getInventory().getItem(newSlot);
        }
        if (itemStack == null) {
            return;
        }
        String localizedString = toLocalizedString(itemStack.getType().getKey());
        JobCurtail jobCurtail = getJobCurtail(player.getName());
        if (jobCurtail == null) {
            return;
        }
        if (!hasBuff(localizedString, jobCurtail.getBuffs())) {
            ExAttributeAPI.getHandleApi().deleteAttribute(player, this.getClass());
        } else {
            List<String> buffs = jobCurtail.getBuffs();
            Gson gson = new Gson();
            List<String> arrayList = new ArrayList<>();
            for (String buff : buffs) {
                buff = buff.substring(localizedString.length() + 1);
                JsonArray array = gson.fromJson(buff, JsonArray.class);
                for (JsonElement jsonElement : array) {
                    arrayList.add(jsonElement.getAsString());
                }
            }
            AttributeData attributeData = new AttributeData(arrayList);
            ExAttributeAPI.getHandleApi().addAttribute(player, this.getClass(), attributeData);
        }
    }

    public boolean hasBuff(String id, List<String> buffs) {
        for (String buff : buffs) {
            if (buff.startsWith(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 合成物品
     *
     * @param event
     */
    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        Recipe recipe = event.getRecipe();
        String localizedString = toLocalizedString(recipe.getResult().getType().getKey());
        HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked instanceof Player player) {
            Job playerJob = getPlayerJob(player.getName());
            if (playerJob == null) {
                event.setCancelled(true);
                return;
            }
            JobCurtail jobCurtail = getJobCurtail(player.getName());
            if (jobCurtail == null) {
                event.setCancelled(true);
                return;
            }
            List<String> recipes = jobCurtail.getRecipes();
            boolean hasPermission = player.hasPermission("JobsCraft.event.jobscraft.craft");
            int localizedExp = 0;
            for (String recipeKey : recipes) {
                if (recipeKey.startsWith(localizedString)) {
                    hasPermission = true;
                    localizedExp = getLocalizedExp(recipeKey);
                    break;
                }
            }
            if (hasPermission) {
                String result = givePlayerExp(player.getName(), playerJob, localizedExp);
                if (result != null) {
                    Messenger.send(player, result);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    /**
     * 造成伤害
     *
     * @param event
     */
    @EventHandler
    public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
        String localizedString = toLocalizedString(event.getEntityType().getKey());
        double damage = event.getDamage();
        if (damage <= 0) {
            return;
        }
        Entity damager = event.getDamager();
        if (damager instanceof Player player) {
            Job playerJob = getPlayerJob(player.getName());
            if (playerJob == null) {
                event.setCancelled(true);
                return;
            }
            JobCurtail jobCurtail = getJobCurtail(player.getName());
            if (jobCurtail == null) {
                event.setCancelled(true);
                return;
            }
            List<String> attacks = jobCurtail.getAttacks();
            boolean hasPermission = player.hasPermission("JobsCraft.event.jobscraft.attack");
            int localizedExp = 0;
            for (String attack : attacks) {
                if (attack.startsWith(localizedString)) {
                    hasPermission = true;
                    localizedExp = getLocalizedExp(attack);
                    break;
                }
            }
            if (hasPermission) {
                String result = givePlayerExp(player.getName(), playerJob, localizedExp);
                if (result != null) {
                    Messenger.send(player, result);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    public String givePlayerExp(String playerName, Job playerJob, int getExps) {
        if (getJobLevelManager().giveExp(playerName, playerJob.getId(), getExps)) {
            // 基于成功
            String tips = JobsCraftLangApi.translate(
                    "gain-exp",
                    String.valueOf(getExps),
                    JobsCraftLangApi.translate("gain-source.break")
            );
            if (!tips.isEmpty()) {
                return tips;
            }
        }
        return null;
    }

    public String getLocalizedName(String content) {
        return content.substring(0, content.lastIndexOf(":"));
    }

    public int getLocalizedExp(String content) {
        return Integer.parseInt(content.substring(content.lastIndexOf(":") + 1));
    }

    public JobManager getJobManager() {
        return JobsCraft.getInstance().getJobManager();
    }

    public JobLevelManager getJobLevelManager() {
        return JobsCraft.getInstance().getJobLevelManager();
    }

    public Job getPlayerJob(String playerName) {
        return getJobManager().getJobByPlayer(playerName);
    }

    public JobLevel getPlayerJobLevel(String playerName) {
        return getJobLevelManager().getJobLevelByPlayer(playerName);
    }

    public JobCurtail getJobCurtail(String playerName) {
        JobCurtailManager jobCurtailManager = JobsCraft.getInstance().getJobCurtailManager();
        Job playerJob = getPlayerJob(playerName);
        if (playerJob == null) {
            return null;
        }
        long jobId = playerJob.getId();
        JobLevel jobLevelByPlayer = getPlayerJobLevel(playerName);
        if (jobLevelByPlayer == null) {
            return null;
        }
        long levelId = jobLevelByPlayer.getId();
        return jobCurtailManager.getJobCurtail(jobId, levelId);
    }

    public String toLocalizedString(NamespacedKey namespacedKey) {
        return namespacedKey.getNamespace() + ":" + namespacedKey.getKey();
    }
}
