package com.zeeyeh.jobscraft.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
    public static <T> List<T> toPaging(int page, int limit, List<T> list) {
        if (page <= 0) {
            page = 1;
        }
        if (list == null || list.size() == 0) {
            return new ArrayList<>(0);
        }
        int startIndex = (page - 1) * limit;
        int endIndex = page * limit;
        int total = list.size();
        int pageCount;
        int num = total % limit;
        if (num == 0) {
            pageCount = total / limit;
        } else {
            pageCount = total / limit + 1;
        }
        if (page == pageCount) {
            endIndex = total;
        }
        return list.subList(startIndex, endIndex);
    }
}
