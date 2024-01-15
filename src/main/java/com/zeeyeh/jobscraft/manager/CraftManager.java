package com.zeeyeh.jobscraft.manager;

import com.zeeyeh.jobscraft.entity.DataSaveType;

import javax.sql.DataSource;
import java.io.File;

public abstract class CraftManager {

    protected DataSaveType type;
    protected DataSource dataSource;
    protected File folder;

    public DataSaveType getType() {
        return type;
    }

    public void setType(DataSaveType type) {
        this.type = type;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }
}
