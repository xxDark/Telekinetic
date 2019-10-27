package ru.xdark.launcher;

@FunctionalInterface
public interface LauncherBootstrapper {
    Launcher create();
}
