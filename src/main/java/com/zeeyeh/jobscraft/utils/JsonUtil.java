package com.zeeyeh.jobscraft.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Json工具类
 */
public class JsonUtil {

    public static List<String> toListString(JsonArray jsonArray) {
        List<String> list = new ArrayList<String>();
        ;
        for (JsonElement jsonElement : jsonArray) {
            list.add(jsonElement.getAsString());
        }
        return list;
    }

    /**
     * 解析json数组字符串
     *
     * @param jsonArrayString json数组字符串
     */
    public static JsonArray toJsonArray(String jsonArrayString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonArrayString, JsonArray.class);
    }

    /**
     * 将list字符串集合转换为json字符串
     *
     * @param list 字符串集合
     */
    public static String toJsonArrayString(List<String> list) {
        JsonArray array = new JsonArray();
        for (String item : list) {
            array.add(item);
        }
        return new Gson().toJson(array);
    }

    /**
     * 解析json对象字符串
     *
     * @param jsonObjectString json对象字符串
     */
    public static JsonObject toJsonObject(String jsonObjectString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObjectString, JsonObject.class);
    }
}
