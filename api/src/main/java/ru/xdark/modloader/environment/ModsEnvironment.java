package ru.xdark.modloader.environment;

import ru.xdark.modloader.di.DependenciesInjector;
import ru.xdark.modloader.event.EventBus;
import ru.xdark.modloader.mod.ModContainer;
import ru.xdark.modloader.resources.ResourceManager;

import java.nio.file.Path;
import java.util.List;

public interface ModsEnvironment {

    Path getWorkingDirectory();

    Path getConfigsDirectory();

    ResourceManager getResourceManager();

    EventBus getEventBus();

    DependenciesInjector getDependenciesInjector();

    List<ModContainer> getLoadedMods();
}
