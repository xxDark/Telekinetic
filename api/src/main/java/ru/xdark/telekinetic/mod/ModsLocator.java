package ru.xdark.telekinetic.mod;

import java.util.Collection;

public interface ModsLocator {
    Collection<ModContainer> findContainers(ModsLocateContext locateContext);
}
