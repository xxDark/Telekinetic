package ru.xdark.modloader.loader;

import ru.xdark.modloader.mod.ModContainer;

import java.util.Collection;

public interface ModsLocator {
    Collection<ModContainer> findContainers();
}
