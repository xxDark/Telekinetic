package ru.xdark.modloader.envinornment;

import ru.xdark.modloader.di.DependenciesInjector;
import ru.xdark.modloader.event.EventBus;
import ru.xdark.modloader.resources.ResourceManager;

import java.nio.file.Path;

public interface ModsEnvironment {

    Path getWorkingDirectory();

    Path getConfigsDirectory();

    ResourceManager getResourceManager();

    EventBus getEventBus();

    DependenciesInjector dependenciesInjector();
}
