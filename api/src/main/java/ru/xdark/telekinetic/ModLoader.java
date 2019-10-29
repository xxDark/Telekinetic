package ru.xdark.telekinetic;

import ru.xdark.launcher.Launcher;
import ru.xdark.telekinetic.di.DependenciesInjector;
import ru.xdark.telekinetic.environment.EnvironmentContext;
import ru.xdark.telekinetic.environment.ModsEnvironment;
import ru.xdark.telekinetic.event.EventBus;
import ru.xdark.telekinetic.mod.Mod;
import ru.xdark.telekinetic.mod.ModContainer;
import ru.xdark.telekinetic.resources.ResourceManager;
import ru.xdark.telekinetic.version.Version;

import java.nio.file.Path;
import java.util.List;

@Mod(
        name = "Telekinetic",
        id = "modLoader",
        version = "1.1.4-BETA",
        authors = "__xDark"
)
public interface ModLoader {

    void inject(Launcher launcher);

    void setup(EnvironmentContext ctx);

    ModsEnvironment getEnvironment();

    Path getWorkingDirectory();

    Path getConfigsDirectory();

    ResourceManager getResourceManager();

    EventBus getEventBus();

    DependenciesInjector getDependenciesInjector();

    List<ModContainer> getLoadedMods();

    Version getVersion();
}
