package ru.xdark.telekinetic.environment;

import lombok.Getter;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.xdark.telekinetic.Side;
import ru.xdark.telekinetic.di.DependenciesInjector;
import ru.xdark.telekinetic.di.ReflectiveDependenciesInjector;
import ru.xdark.telekinetic.event.EventBus;
import ru.xdark.telekinetic.event.SimpleEventBus;
import ru.xdark.telekinetic.mod.ModContainer;
import ru.xdark.telekinetic.version.Version;

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
    private final List<ModContainer> loadedMods;
    @Getter
    private final Version version;
    @Getter
    private final Side side;
    private final Map<Object, ModContainer> containerByInstancesMap;

    public DefaultModsEnvironment(EnvironmentContext context) {
        Objects.requireNonNull(context, "context");
        val workingDirectory = context.workingDirectory;
        this.workingDirectory = Objects.requireNonNull(context.workingDirectory, "workingDirectory");
        this.configsDirectory = workingDirectory.resolve("configs");
        this.eventBus = new SimpleEventBus(context.loader);
        this.dependenciesInjector = createInjector();
        val loadedMods = context.loadedMods;
        this.loadedMods = Collections.unmodifiableList(loadedMods);
        this.version = context.loaderVersion;
        this.side = context.side;
        this.containerByInstancesMap = loadedMods.stream().collect(Collectors.toMap(ModContainer::getInstance, Function.identity()));
    }

    @Override
    public ModContainer findContainer(Object mod) {
        return this.containerByInstancesMap.get(mod);
    }

    private DependenciesInjector createInjector() {
        val injector = new ReflectiveDependenciesInjector();
        injector.registerInjector(ModsEnvironment.class, __ -> this);
        injector.registerInjector(EventBus.class, __ -> eventBus);
        injector.registerInjector(Logger.class, LogManager::getLogger);
        injector.registerInjector(ModContainer.class, this::findContainer);
        return injector;
    }
}
