package ru.xdark.launcher;

import java.util.Set;

public interface Launcher extends ClassLoadingController, ClasspathAppender, NativeLibrariesAppender {

    void inject(LauncherClassLoader classLoader);

    void registerTweaker(Tweaker tweaker);

    void injectTweakers(LauncherInitializationContext context);

    void setLaunchTarget(String target);

    String getLaunchTarget();

    Set<LauncherOption> getLauncherOptions();

    boolean isOptionSet(LauncherOption option);

    default void gotoPhase(LaunchPhase phase) { }
}
