package ru.xdark.modloader.loader;

import me.xdark.launcher.Launcher;
import me.xdark.launcher.LauncherClassLoader;
import ru.xdark.modloader.mod.ModContainer;

import java.util.Collection;

public interface ModsLocator {
    Collection<ModContainer> findContainers(Launcher launcher, LauncherClassLoader classLoader);
}
