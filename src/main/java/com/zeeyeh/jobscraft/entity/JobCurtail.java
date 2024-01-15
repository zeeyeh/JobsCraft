package com.zeeyeh.jobscraft.entity;

import java.util.List;

public class JobCurtail {
    private long id;
    private String name;
    private long jobId;
    private long levelId;
    private List<String> tools;
    private List<String> foods;
    private List<String> destructs;
    private List<String> places;
    private List<String> recipes;
    private List<String> interacts;
    private List<String> attacks;
    private List<String> buffs;

    public JobCurtail(long id, String name, long jobId, long levelId, List<String> tools, List<String> foods, List<String> destructs, List<String> places, List<String> recipes, List<String> interacts, List<String> attacks, List<String> buffs) {
        this.id = id;
        this.name = name;
        this.jobId = jobId;
        this.levelId = levelId;
        this.tools = tools;
        this.foods = foods;
        this.destructs = destructs;
        this.places = places;
        this.recipes = recipes;
        this.interacts = interacts;
        this.attacks = attacks;
        this.buffs = buffs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public long getLevelId() {
        return levelId;
    }

    public void setLevelId(long levelId) {
        this.levelId = levelId;
    }

    public List<String> getTools() {
        return tools;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }

    public List<String> getFoods() {
        return foods;
    }

    public void setFoods(List<String> foods) {
        this.foods = foods;
    }

    public List<String> getDestructs() {
        return destructs;
    }

    public void setDestructs(List<String> destructs) {
        this.destructs = destructs;
    }

    public List<String> getPlaces() {
        return places;
    }

    public void setPlaces(List<String> places) {
        this.places = places;
    }

    public List<String> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<String> recipes) {
        this.recipes = recipes;
    }

    public List<String> getInteracts() {
        return interacts;
    }

    public void setInteracts(List<String> interacts) {
        this.interacts = interacts;
    }

    public List<String> getAttacks() {
        return attacks;
    }

    public void setAttacks(List<String> attacks) {
        this.attacks = attacks;
    }

    public List<String> getBuffs() {
        return buffs;
    }

    public void setBuffs(List<String> buffs) {
        this.buffs = buffs;
    }
}
