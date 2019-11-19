package ru.xdark.telekinetic.environment;

import ru.xdark.telekinetic.Side;
import ru.xdark.telekinetic.di.DependenciesInjector;
import ru.xdark.telekinetic.event.EventBus;
import ru.xdark.telekinetic.mod.ModContainer;
import ru.xdark.telekinetic.resources.Resource;
import ru.xdark.telekinetic.resources.ResourceManager;
import ru.xdark.telekinetic.version.Version;

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

    ModContainer findContainer(Object mod);

    Side getSide();

    List<Resource> findResources(Object mod, String path);

    boolean hasResource(Object mod, String path);
}
