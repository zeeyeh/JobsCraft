package com.zeeyeh.jobscraft.entity;

import java.util.List;

public class Job {
    private long id;
    private String name;
    private String title;
    private List<String> players;

    public Job(long id, String name, String title) {
        this.id = id;
        this.name = name;
        this.title = title;
    }

    public Job(long id, String name, String title, List<String> players) {
        this.id = id;
        this.name = name;
        this.title = title;
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

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}
