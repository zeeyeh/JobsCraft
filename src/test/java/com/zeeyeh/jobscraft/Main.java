package com.zeeyeh.jobscraft;

import com.zeeyeh.jobscraft.utils.ListUtil;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14");
        List<String> listPaging = ListUtil.toPaging(0, 5, list);
        for (String result : listPaging) {
            System.out.println(result);
        }
    }
}
