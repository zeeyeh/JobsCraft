package com.zeeyeh.devtoolkit.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    public static String getContent(InputStream inputStream, Charset charset) {
        try {
            int available = inputStream.available();
            Method method = Files.class.getDeclaredMethod("read", InputStream.class, int.class);
            method.setAccessible(true);
            return new String((byte[]) method.invoke(null, inputStream, available), charset);
        } catch (Exception e) {
            e.fillInStackTrace();
            return null;
        }
    }

    public static String getContent(String filepath, Charset charset) {
        try {
            return Files.readString(Path.of(filepath), charset);
        } catch (IOException e) {
            e.fillInStackTrace();
            return null;
        }
    }

    public static String getContent(File file, Charset charset) {
        try {
            return Files.readString(file.toPath(), charset);
        } catch (IOException e) {
            e.fillInStackTrace();
            return null;
        }
    }
}
