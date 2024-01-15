package com.zeeyeh.jobscraft.utils;

import com.zeeyeh.devtoolkit.message.Messenger;
import com.zeeyeh.jobscraft.JobsCraft;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class LibrariesUtil {
    private static final String PREFIX_URL = "https://maven.aliyun.com/repository/central/";

    /**
     * 加载库文件
     *
     * @param groupId    包名
     * @param artifactId 库名
     * @param version    版本
     * @param folder     所有库保存目录
     * @return 库文件对象
     */
    public static File loadFile(String groupId, String artifactId, String version, File folder) {
        String url = mergeUrl(groupId, artifactId, version);
        File saveFile = getSaveFile(folder, url);
        Messenger.send(Bukkit.getConsoleSender(), "Loading library... " + saveFile.getName());
        if (saveFile.exists()) {
            return saveFile;
        }
        return downloadFile(groupId, artifactId, version, folder);
    }

    /**
     * 下载库文件
     *
     * @param groupId    包名
     * @param artifactId 库名
     * @param version    版本
     * @param folder     所有库保存目录
     * @return 库文件对象
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File downloadFile(String groupId, String artifactId, String version, File folder) {
        String url = mergeUrl(groupId, artifactId, version);
        File saveFile = getSaveFile(folder, url);
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        try {
            FileUtils.copyURLToFile(new URL(url), saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(JobsCraft.getInstance());
            return null;
        }
        //HttpUtil.downloadFile(url, saveFile, new StreamProgress() {
        //    @Override
        //    public void start() {
        //        Messenger.send(Bukkit.getConsoleSender(), "Start downloading library " + saveFile.getName());
        //    }
        //
        //    @Override
        //    public void progress(long total, long progressSize) {
        //        long schedule = (total / progressSize) * 100;
        //        Messenger.send(Bukkit.getConsoleSender(), schedule + "%/100% Downloading library " + saveFile.getName() + ". Completed: " + progressSize + "/" + total);
        //    }
        //
        //    @Override
        //    public void finish() {
        //        Messenger.send(Bukkit.getConsoleSender(), "Done download library " + saveFile.getName());
        //    }
        //});
        return saveFile;
    }

    /**
     * 获取保存路径
     *
     * @param folder 保存目录
     * @param url    库下载地址
     * @return 库文件对象
     */
    public static File getSaveFile(File folder, String url) {
        return new File(folder, url.replace(PREFIX_URL, ""));
    }

    /**
     * 合并url下载地址
     *
     * @param groupId    包名
     * @param artifactId 库名
     * @param version    版本
     * @return 库下载地址
     */
    public static String mergeUrl(String groupId, String artifactId, String version) {
        return PREFIX_URL + groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/' + artifactId + '-' + version + ".jar";
    }
}
