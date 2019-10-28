package ru.xdark.telekenetic.environment;

import ru.xdark.telekenetic.di.DependenciesInjector;
import ru.xdark.telekenetic.event.EventBus;
import ru.xdark.telekenetic.mod.ModContainer;
import ru.xdark.telekenetic.resources.Resource;
import ru.xdark.telekenetic.resources.ResourceManager;
import ru.xdark.telekenetic.version.Version;

import java.nio.file.Path;
import java.util.List;

public interface ModsEnvironment {

    Path getWorkingDirectory();

    Path getConfigsDirectory();

    ResourceManager getResourceManager();

    EventBus getEventBus();

    DependenciesInjector getDependenciesInjector();

    List<ModContainer> getLoadedMods();

    Version getVersion();

    List<Resource> findResources(Object mod, String path);

    boolean hasResource(Object mod, String path);
}
