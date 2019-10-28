package ru.xdark.modloader;

import lombok.experimental.UtilityClass;
import ru.xdark.launcher.Launcher;
import ru.xdark.modloader.di.DependenciesInjector;
import ru.xdark.modloader.envinornment.ModsEnvironment;
import ru.xdark.modloader.environment.DefaultModsEnvironment;
import ru.xdark.modloader.environment.EnvironmentContext;
import ru.xdark.modloader.event.EventBus;
import ru.xdark.modloader.mod.ModContainer;
import ru.xdark.modloader.resources.ResourceManager;

import java.nio.file.Path;
import java.util.List;

@UtilityClass
public class Modloader {

    private ModsEnvironment environment;

    public void inject(Launcher launcher) {
        launcher.registerTweaker(new ModloaderTweaker());
    }

    public void setup(EnvironmentContext context) {
        environment = new DefaultModsEnvironment(context);
    }

    public ModsEnvironment environment() {
        return Modloader.environment;
    }

    public Path workingDirectory() {
        return environment.getWorkingDirectory();
    }

    public Path configsDirectory() {
        return environment.getConfigsDirectory();
    }

    public ResourceManager resourceManager() {
        return environment.getResourceManager();
    }

    public EventBus bus() {
        return environment.getEventBus();
    }

    public DependenciesInjector dependenciesInjector() {
        return environment.getDependenciesInjector();
    }

    public List<ModContainer> loadedMods() {
        return environment.getLoadedMods();
    }
}
