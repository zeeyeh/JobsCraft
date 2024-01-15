package com.zeeyeh.jobscraft.event.job;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobCreateEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private long id;
    private String name;
    private String title;

    public JobCreateEvent(long id, String name, String title) {
        this.id = id;
        this.name = name;
        this.title = title;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }
}
