package ru.xdark.launcher;

import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;

public interface Launcher extends ClassLoadingController, ClasspathAppender, NativeLibrariesAppender {

    void inject(LauncherClassLoader classLoader);

    void registerTweaker(Tweaker tweaker);

    void injectTweakers(LauncherInitializationContext context);

    void setLaunchTarget(String target);

    String getLaunchTarget();

    Set<LauncherOption> getLauncherOptions();

    Properties getProperties();

    void setProperty(Object key, Object value);

    <T> T getProperty(Object key);

    <T> T getProperty(Object key, T defaultValue);

    <T> T getProperty(Object key, Supplier<T> defaultValueSupplier);

    boolean isOptionSet(LauncherOption option);

    default void gotoPhase(LaunchPhase phase) { }
}
