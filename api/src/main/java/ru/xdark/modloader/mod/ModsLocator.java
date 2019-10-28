package ru.xdark.modloader.mod;

import ru.xdark.launcher.Launcher;
import ru.xdark.launcher.LauncherClassLoader;

import java.util.Collection;

public interface ModsLocator {
    Collection<ModContainer> findContainers(Launcher launcher, LauncherClassLoader classLoader);
}
