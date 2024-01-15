package com.zeeyeh.jobscraft.factory;

import com.zeeyeh.jobscraft.entity.Job;
import com.zeeyeh.jobscraft.utils.IdUtil;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 职业构建工厂
 */
public class JobFactory {

    public static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]+");
    private long id;
    private String name;
    private String title;
    private List<String> players;

    /**
     * 获取职业构建工厂
     */
    public static JobFactory create() {
        return new JobFactory();
    }

    /**
     * 设置职业唯一标识Id
     *
     * @param id 唯一id
     */
    public JobFactory id(long id) {
        this.id = id;
        return this;
    }

    /**
     * 设置职业标识名称
     *
     * @param name 名称
     */
    public JobFactory name(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置该职业暂时标题
     *
     * @param title 标题内容
     */
    public JobFactory title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置所处该职业玩家
     *
     * @param players 玩家名称列表
     */
    public JobFactory players(List<String> players) {
        this.players = players;
        return this;
    }

    /**
     * 构建职业实例
     */
    public Job build() {
        Matcher matcher = NAME_PATTERN.matcher(name);
        if (!matcher.matches()) {
            // 名称不符合规则
            return null;
        }
        return new Job(id, name, title, players);
    }

    /**
     * 创建一个职业
     *
     * @param name  职业名称
     * @param title 职业标题
     */
    public static Job createJob(String name, String title) {
        return createJob(IdUtil.generateId(), name, title);
    }

    /**
     * 创建一个职业
     *
     * @param id    职业唯一Id
     * @param name  职业名称
     * @param title 职业标题
     */
    public static Job createJob(long id, String name, String title) {
        return createJob(id, name, title, Collections.emptyList());
    }

    /**
     * 创建一个职业
     *
     * @param id      职业唯一Id
     * @param name    职业名称
     * @param title   职业标题
     * @param players 玩家数量
     */
    public static Job createJob(long id, String name, String title, List<String> players) {
        return JobFactory.create()
                .id(id)
                .name(name)
                .title(title)
                .players(players)
                .build();
    }

    /**
     * 校验名称是否符合规则
     *
     * @param name 待校验名称
     * @return 是否符合
     */
    public static boolean checkName(String name) {
        Matcher matcher = NAME_PATTERN.matcher(name);
        return matcher.matches();
    }
}
