package com.zeeyeh.devtoolkit.util;

import com.zeeyeh.devtoolkit.message.ColorFactory;
import com.zeeyeh.devtoolkit.message.ColorFactoryWrapper;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 颜色工具类
 */
public class ColorUtil {

    /**
     * 格式化文本颜色
     *
     * @param message 文本内容
     */
    public static String translate(String message) {
        return translate(
                '&', message
        );
    }

    /**
     * 格式化文本颜色
     *
     * @param colorChar 颜色替代字符
     * @param message   文本内容
     */
    public static String translate(char colorChar, String message) {
        return translate(colorChar, message, false);
    }

    /**
     * 格式化文本颜色
     *
     * @param colorChar 颜色替代字符
     * @param message   文本内容
     * @param hex       是否支持16进制渐变颜色表达式
     */
    public static String translate(char colorChar, String message, boolean hex) {
        if (hex) {
            Pattern pattern = Pattern.compile("^\\[#([A-z0-9]+)](.*)+\\[#([A-z0-9]+)]$");
            Matcher matcher = pattern.matcher(message);
            if (matcher.matches()) {
                String javaVersion = System.getProperty("java.version");
                if (!javaVersion.contains("17")) {
                    throw new RuntimeException("Current Java version " + javaVersion + " is not supported. The Java version must be 14+");
                } else {
                    while (matcher.find()) {
                        String startColor = matcher.group(1);
                        String content = matcher.group(2);
                        String endColor = matcher.group(3);
                        message = translateHex(toGradient(content, startColor, endColor, false));
                    }
                }
            }
        }
        message = ChatColor.translateAlternateColorCodes(
                colorChar, message
        );
        return message;
    }

    /**
     * 格式化16进制渐变色
     *
     * @param message 消息文本
     */
    public static String translateHex(String message) {
        Pattern pattern = Pattern.compile("\\[#([A-Fa-f0-9]{6})]");
        Matcher matcher = pattern.matcher(message);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            String color = matcher.group(1);
            StringBuilder replacement = new StringBuilder(Character.toString(167)).append("x");
            for (int i = 0; i < color.length(); i++) {
                replacement.append(Character.toString(167)).append(color.charAt(i));
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 格式化渐变色文本
     *
     * @param message    消息文本
     * @param startColor 渐变开始颜色
     * @param endColor   渐变结束颜色
     * @param isBold     是否加粗
     */
    public static String toGradient(String message, String startColor, String endColor, boolean isBold) {
        return toGradient(message, startColor, endColor, isBold, (origin, text) -> {
            if (text.contains("&k")) {
                origin.append("&k");
                text = text.replace("&k", "");
            }
            if (text.contains("&m")) {
                origin.append("&m");
                text = text.replace("&m", "");
            }
            if (text.contains("&n")) {
                origin.append("&n");
                text = text.replace("&n", "");
            }
            if (text.contains("&l")) {
                origin.append("&l");
                text = text.replace("&l", "");
            }
            return new ColorFactoryWrapper(origin, text);
        });
    }

    /**
     * 格式化渐变色文本
     *
     * @param message    消息文本
     * @param startColor 渐变开始颜色
     * @param endColor   渐变结束颜色
     * @param isBold     是否加粗
     * @param func       残余格式处理工厂
     */
    public static String toGradient(String message, String startColor, String endColor, boolean isBold, ColorFactory<StringBuilder, String, ColorFactoryWrapper> func) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            int progress = i / (message.length() - 1);
            String color = formatColor(startColor, endColor, progress);
            result.append("[#").append(color).append("]");
            if (isBold) {
                result.append("&l");
            }
            ColorFactoryWrapper cfw = func.apply(result, message);
            result = cfw.text();
            message = cfw.content();
            result.append(message.charAt(i));
        }
        return result.toString();
    }

    /**
     * 计算处理渐变色
     *
     * @param startColor 渐变开始颜色
     * @param endColor   渐变结束颜色
     * @param progress   渐变进度
     */
    private static String formatColor(String startColor, String endColor, float progress) {
        int startRed = Integer.parseInt(startColor.substring(0, 2), 16);
        int startGreen = Integer.parseInt(startColor.substring(2, 4), 16);
        int startBlue = Integer.parseInt(startColor.substring(4, 6), 16);
        int endRed = Integer.parseInt(endColor.substring(0, 2), 16);
        int endGreen = Integer.parseInt(endColor.substring(2, 4), 16);
        int endBlue = Integer.parseInt(endColor.substring(4, 6), 16);
        int red = (int) (startRed + ((endRed - startRed) * progress));
        int green = (int) (startGreen + ((endGreen - startGreen) * progress));
        int blue = (int) (startBlue + ((endBlue - startBlue) * progress));
        return String.format("%02x%02x%02x", red, green, blue);
    }
}
