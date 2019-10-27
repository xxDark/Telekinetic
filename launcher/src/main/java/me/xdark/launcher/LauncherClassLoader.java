package me.xdark.launcher;

import lombok.experimental.Delegate;

import java.net.URL;
import java.net.URLClassLoader;

public final class LauncherClassLoader extends URLClassLoader implements Launcher {
    @Delegate(types = Launcher.class)
    private final Launcher handle;
    private final ClassLoader parent;

    LauncherClassLoader(Launcher handle, URL[] urls, ClassLoader parent) {
        super(urls, null);
        this.parent = parent;
        this.handle = handle;
        initialize(handle);
    }

    private static void initialize(Launcher launcher) {
        launcher.addClassLoadingExclusions(
                "java.",
                "sun.",
                "com.sun.",
                "me.xdark.modloader.",
                "org.apache.logging."
        );
        launcher.addTransformerExclusions(
                "javax.",
                "org.objectweb.",
                "com.google."
        );
    }
}
