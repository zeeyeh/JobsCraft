package com.zeeyeh.jobscraft.entity;

public enum JobsCraftTableNames {
    /**
     * 职业数据库名
     */
    JOB("jobs"),
    /**
     * 职业等级数据库名
     */
    JOB_LEVEL("levels"),
    /**
     * 职业限制数据库名
     */
    JOB_CURTAIL("curtails"),
    ;
    private String name;

    JobsCraftTableNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
