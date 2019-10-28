package ru.xdark.launcher;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;

@UtilityClass
public class NativeUtil {

    public void addPathToNatives(Path path) {
        val stringPath = path.normalize().toString();
        String libraryPath = System.getProperty("java.library.path");
        if (libraryPath == null) {
            libraryPath = stringPath;
        } else {
            libraryPath += File.pathSeparatorChar + stringPath;
        }
        System.setProperty("java.library.path", libraryPath);
    }

    public void addPathsToNatives(Path... paths) {
        if (paths.length == 0) return;
        val cp = File.pathSeparatorChar;
        val builder = new StringBuilder(64);
        for (val path : paths) {
            builder.append(path.normalize().toString()).append(cp);
        }
        builder.deleteCharAt(builder.length() - 1);
        String libraryPath = System.getProperty("java.library.path");
        if (libraryPath == null) {
            libraryPath = builder.toString();
        } else {
            libraryPath += File.pathSeparatorChar + builder.toString();
        }
        System.setProperty("java.library.path", libraryPath);
    }

    @SneakyThrows
    public void resetNativeCaches() {
        Field field = ClassLoader.class.getDeclaredField("sys_paths");
        field.setAccessible(true);
        field.set(null, null);
        field = ClassLoader.class.getDeclaredField("usr_paths");
        field.setAccessible(true);
        field.set(null, null);
    }
}
