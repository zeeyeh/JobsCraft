package com.zeeyeh.devtoolkit.command;

import com.zeeyeh.devtoolkit.annotation.SubCommander;

import java.lang.reflect.Method;

public class SubCommandEntity {
    private String name;
    private Method instance;
    private SubCommander description;

    public SubCommandEntity() {
    }

    public SubCommandEntity(String name, Method instance, SubCommander description) {
        this.name = name;
        this.instance = instance;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Method getInstance() {
        return instance;
    }

    public void setInstance(Method instance) {
        this.instance = instance;
    }

    public SubCommander getDescription() {
        return description;
    }

    public void setDescription(SubCommander description) {
        this.description = description;
    }
}
