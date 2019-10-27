package me.xdark.launcher;

@FunctionalInterface
public interface LauncherBootstrapper {
    Launcher create();
}
