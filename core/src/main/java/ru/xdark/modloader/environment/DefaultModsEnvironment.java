package ru.xdark.modloader.environment;

import lombok.Getter;
import lombok.val;
import ru.xdark.modloader.di.DependenciesInjector;
import ru.xdark.modloader.di.ReflectiveDependenciesInjector;
import ru.xdark.modloader.envinornment.ModsEnvironment;
import ru.xdark.modloader.event.EventBus;
import ru.xdark.modloader.event.SimpleEventBus;
import ru.xdark.modloader.mod.ModContainer;
import ru.xdark.modloader.resources.ResourceManager;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DefaultModsEnvironment implements ModsEnvironment {

    @Getter
    private final Path workingDirectory;
    @Getter
    private final Path configsDirectory;
    @Getter
    private final EventBus eventBus;
    @Getter
    private final DependenciesInjector dependenciesInjector;
    @Getter
    private final ResourceManager resourceManager;
    @Getter
    private final List<ModContainer> loadedMods;

    public DefaultModsEnvironment(EnvironmentContext context) {
        Objects.requireNonNull(context, "context");
        val workingDirectory = context.workingDirectory;
        this.workingDirectory = Objects.requireNonNull(context.workingDirectory, "workingDirectory");
        this.configsDirectory = workingDirectory.resolve("configs");
        this.eventBus = new SimpleEventBus(context.loader);
        this.dependenciesInjector = createInjector();
        this.resourceManager = null;
        this.loadedMods = Collections.unmodifiableList(context.loadedMods);
    }

    private DependenciesInjector createInjector() {
        val injector = new ReflectiveDependenciesInjector();
        injector.registerInjector(ModsEnvironment.class, __ -> this);
        injector.registerInjector(EventBus.class, __ -> eventBus);
        injector.registerInjector(ResourceManager.class, __ -> resourceManager);
        return injector;
    }
}
