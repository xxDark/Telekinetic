package ru.xdark.telekenetic.mod;

import java.util.Collection;

public interface ModsLocator {
    Collection<ModContainer> findContainers(ModsLocateContext locateContext);
}
