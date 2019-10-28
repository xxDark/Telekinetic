package ru.xdark.modloader;

import lombok.val;
import ru.xdark.launcher.Launcher;
import ru.xdark.modloader.di.DependenciesInjector;
import ru.xdark.modloader.environment.ModsEnvironment;
import ru.xdark.modloader.environment.DefaultModsEnvironment;
import ru.xdark.modloader.environment.EnvironmentContext;
import ru.xdark.modloader.event.EventBus;
import ru.xdark.modloader.mod.Mod;
import ru.xdark.modloader.mod.ModContainer;
import ru.xdark.modloader.resources.Resource;
import ru.xdark.modloader.resources.ResourceManager;
import ru.xdark.modloader.resources.ResourcesContainer;

import java.nio.file.Path;
import java.util.List;

@Mod(
        name = "Modloader",
        id = "modloader",
        version = "1.0-BETA",
        authors = "__xDark"
)
public final class Modloader implements ResourcesContainer {

    private static final Modloader instance = new Modloader();
    private ModsEnvironment environment;

    public void inject(Launcher launcher) {
        launcher.registerTweaker(new ModloaderTweaker(this));
    }

    public void setup(EnvironmentContext context) {
        val environment = this.environment = new DefaultModsEnvironment(context);
        val bus = environment.getEventBus();
        val injector = environment.getDependenciesInjector();
        for (val mod : environment.getLoadedMods()) {
            val instance = mod.getInstance();
            bus.registerListeners(instance);
            injector.inject(injector);
        }
    }

    public ModsEnvironment environment() {
        return environment;
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

    public static Modloader instance() {
        return instance;
    }

    @Override
    public List<Resource> findResources(String path) {
        return resourceManager().findResources(path);
    }

    @Override
    public boolean hasResource(String path) {
        return resourceManager().hasResource(path);
    }
}
