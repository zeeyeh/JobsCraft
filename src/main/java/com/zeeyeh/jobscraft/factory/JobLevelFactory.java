package com.zeeyeh.jobscraft.factory;

import com.zeeyeh.jobscraft.entity.JobLevel;
import com.zeeyeh.jobscraft.utils.IdUtil;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 职业等级构建工厂
 */
public class JobLevelFactory {

    public static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]+");
    private long id;
    private String name;
    private String title;
    private long maxExp;
    private long nextId;
    private List<String> players;

    /**
     * 获取职业等级构建工厂
     */
    public static JobLevelFactory create() {
        return new JobLevelFactory();
    }

    /**
     * 设置职业等级唯一标识Id
     *
     * @param id 唯一id
     */
    public JobLevelFactory id(long id) {
        this.id = id;
        return this;
    }

    /**
     * 设置职业等级标识名称
     *
     * @param name 名称
     */
    public JobLevelFactory name(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置该职业等级展示标题
     *
     * @param title 标题内容
     */
    public JobLevelFactory title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置该职业等级最大经验值
     *
     * @param maxExp 最大经验值
     */
    public JobLevelFactory maxExp(long maxExp) {
        this.maxExp = maxExp;
        return this;
    }

    /**
     * 设置该职业等级下一阶段职业等级Id
     *
     * @param nextId 职业等级Id
     */
    public JobLevelFactory nextId(long nextId) {
        this.nextId = nextId;
        return this;
    }

    /**
     * 设置所处该职业等级玩家
     *
     * @param players 玩家名称列表
     */
    public JobLevelFactory players(List<String> players) {
        this.players = players;
        return this;
    }

    /**
     * 构建职业等级实例
     */
    public JobLevel build() {
        Matcher matcher = NAME_PATTERN.matcher(name);
        if (!matcher.matches()) {
            // 名称不符合规则
            return null;
        }
        return new JobLevel(id, name, title, maxExp, nextId, players);
    }

    /**
     * 创建一个职业等级
     *
     * @param name  职业等级名称
     * @param title 职业等级标题
     */
    public static JobLevel createJobLevel(String name, String title, long maxExp, long nextId) {
        return createJobLevel(IdUtil.generateId(), name, title, maxExp, nextId);
    }

    /**
     * 创建一个职业等级
     *
     * @param id    职业等级唯一Id
     * @param name  职业等级名称
     * @param title 职业等级标题
     */
    public static JobLevel createJobLevel(long id, String name, String title, long maxExp, long nextId) {
        return createJobLevel(id, name, title, maxExp, nextId, Collections.emptyList());
    }

    /**
     * 创建一个职业等级
     *
     * @param id      职业等级唯一Id
     * @param name    职业等级名称
     * @param title   职业等级标题
     * @param players 玩家数量
     */
    public static JobLevel createJobLevel(long id, String name, String title, long maxExp, long nextId, List<String> players) {
        return JobLevelFactory.create()
                .id(id)
                .name(name)
                .title(title)
                .maxExp(maxExp)
                .nextId(nextId)
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
