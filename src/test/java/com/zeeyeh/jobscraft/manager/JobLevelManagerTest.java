package com.zeeyeh.jobscraft.manager;

import junit.framework.TestCase;

public class JobLevelManagerTest extends TestCase {

    public void testGetPlayerName() {
        String line = "asdsdas:1232134";
        System.out.println(getPlayerName(line));
    }

    public void testGetPlayerExp() {
        String line = "asdsdas:1232134";
        System.out.println(getPlayerExp(line));
    }


    public String getPlayerName(String playerLine) {
        return playerLine.substring(0, playerLine.indexOf(":"));
    }

    public String getPlayerExp(String playerLine) {
        return playerLine.substring(playerLine.indexOf(":") + 1);
    }
}