package com.zeeyeh.jobscraft.api;

import com.zeeyeh.devtoolkit.config.Configuration;
import com.zeeyeh.devtoolkit.config.locales.LanguageManager;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobsCraftLangApi {
    private static LanguageManager languageManager;

    public static void initLanguage(LanguageManager languageManager) {
        JobsCraftLangApi.languageManager = languageManager;
    }

    @Deprecated(forRemoval = true)
    public static List<String> translateList(String path, List<String[]> params) {
        Map<String, Configuration> langConfigs = languageManager.getLangConfigs();
        String langName = languageManager.getLangName();
        if (!langConfigs.containsKey(langName)) {
            return Collections.emptyList();
        }
        Configuration configuration = langConfigs.get(langName);
        List<String> langContents = configuration.getStringList(path);
        List<String> langs = new ArrayList<>();
        for (int i = 0; i < langContents.size(); i++) {
            langs.add(mergeParams(langContents.get(i), params.get(i)));
        }
        return langs;
    }

    @Deprecated(forRemoval = true)
    public static List<String[]> buildParams(String[]... params) {
        return new ArrayList<>(Arrays.asList(params));
    }

    public static String translate(String path, String... params) {
        String langContent = languageManager.getLang(path);
        return mergeParams(langContent, params);
    }

    public static String mergeParams(String content, String... params) {
        Pattern pattern = Pattern.compile("\\{(\\d+)}");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            int count = 1;
            while (matcher.find()) {
                count++;
            }
            String[] langParams = new String[count];
            if (params.length < count) {
                System.arraycopy(params, 0, langParams, 0, params.length);
                for (int i = params.length; i < count; i++) {
                    langParams[i] = "";
                }
            } else {
                langParams = Arrays.copyOf(params, params.length);
            }
            content = languageManager.parseParams(content, langParams);
        }
        return content;
    }
}
