package ru.xdark.telekenetic;

import ru.xdark.launcher.Launcher;
import ru.xdark.telekenetic.di.DependenciesInjector;
import ru.xdark.telekenetic.environment.EnvironmentContext;
import ru.xdark.telekenetic.environment.ModsEnvironment;
import ru.xdark.telekenetic.event.EventBus;
import ru.xdark.telekenetic.mod.Mod;
import ru.xdark.telekenetic.mod.ModContainer;
import ru.xdark.telekenetic.resources.ResourceManager;
import ru.xdark.telekenetic.version.Version;

import java.nio.file.Path;
import java.util.List;

@Mod(
        name = "Modloader",
        id = "modloader",
        version = "1.1.3-BETA",
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
