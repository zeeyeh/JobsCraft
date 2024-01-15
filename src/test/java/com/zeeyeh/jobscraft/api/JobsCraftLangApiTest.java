package com.zeeyeh.jobscraft.api;

import junit.framework.TestCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobsCraftLangApiTest extends TestCase {

    public void testTranslate() {
        String translate = translate("", "&a&l每位玩家最多{2}能胜任{0}个{1}职业", "7", "6", "5");
        System.out.println(translate);
    }

    public String translate(String path, String str, String... params) {
        String langContent = str;
        Pattern pattern = Pattern.compile("\\{(\\d+)}");
        Matcher matcher = pattern.matcher(langContent);
        if (matcher.find()) {
            int count = 1;
            while (matcher.find()) {
                count++;
            }
            String[] langParams = new String[count];
            if (params.length < count) {
                System.arraycopy(params, 0, langParams, 0, params.length);
                for (int i = params.length; i < count; i++) {
                    langParams[i] = " null ";
                }
            } else {
                System.arraycopy(params, 0, langParams, 0, count);
            }
            langContent = parseParams(langContent, langParams);
        }
        return langContent;
    }

    public String parseParams(String content, String... params) {
        for (int i = 0; i < params.length; ++i) {
            content = content.replace("{" + i + "}", params[i]);
        }

        return content;
    }
}