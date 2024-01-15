package com.zeeyeh.jobscraft.entity;

import java.util.Collections;
import java.util.List;

public class JobLevel {
    private long id;
    private String name;
    private String title;
    private long maxExp;
    private long nextId;
    private List<String> players;

    public JobLevel(long id, String name, String title, long maxExp) {
        this(id, name, title, maxExp, -1000);
    }

    public JobLevel(long id, String name, String title, long maxExp, long nextId) {
        this(id, name, title, maxExp, nextId, Collections.emptyList());
    }

    public JobLevel(long id, String name, String title, long maxExp, long nextId, List<String> players) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.maxExp = maxExp;
        this.nextId = nextId;
        this.players = players;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(long maxExp) {
        this.maxExp = maxExp;
    }

    public long getNextId() {
        return nextId;
    }

    public void setNextId(long nextId) {
        this.nextId = nextId;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}
