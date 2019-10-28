package ru.xdark.telekenetic.environment;

import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.val;
import ru.xdark.telekenetic.di.DependenciesInjector;
import ru.xdark.telekenetic.di.ReflectiveDependenciesInjector;
import ru.xdark.telekenetic.event.EventBus;
import ru.xdark.telekenetic.event.SimpleEventBus;
import ru.xdark.telekenetic.mod.ModContainer;
import ru.xdark.telekenetic.resources.ResourceManager;
import ru.xdark.telekenetic.version.Version;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @Delegate(types = ResourceManager.class)
    private final ResourceManager resourceManager;
    @Getter
    private final List<ModContainer> loadedMods;
    @Getter
    private final Version version;
    private final Map<Object, ModContainer> containerByInstancesMap;

    public DefaultModsEnvironment(EnvironmentContext context) {
        Objects.requireNonNull(context, "context");
        val workingDirectory = context.workingDirectory;
        this.workingDirectory = Objects.requireNonNull(context.workingDirectory, "workingDirectory");
        this.configsDirectory = workingDirectory.resolve("configs");
        this.eventBus = new SimpleEventBus(context.loader);
        this.dependenciesInjector = createInjector();
        this.resourceManager = null;
        val loadedMods = context.loadedMods;
        this.loadedMods = Collections.unmodifiableList(loadedMods);
        this.containerByInstancesMap = loadedMods.stream().collect(Collectors.toMap(ModContainer::getInstance, Function.identity()));
        this.version = context.loaderVersion;
    }

    private DependenciesInjector createInjector() {
        val injector = new ReflectiveDependenciesInjector();
        injector.registerInjector(ModsEnvironment.class, __ -> this);
        injector.registerInjector(EventBus.class, __ -> eventBus);
        injector.registerInjector(ResourceManager.class, __ -> resourceManager);
        return injector;
    }
}
