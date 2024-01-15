package com.zeeyeh.jobscraft.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Map字典工具类
 */
public class MapUtil {
    /**
     * 对象实例转换为Map字典
     *
     * @param object 对象实例
     */
    public static Map<String, Object> asMap(String key, Object object) {
        Map<String, Object> dataMap = new HashMap<>();
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                dataMap.put(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<String, Object>() {
            {
                put(key, dataMap);
            }
        };
    }
}
