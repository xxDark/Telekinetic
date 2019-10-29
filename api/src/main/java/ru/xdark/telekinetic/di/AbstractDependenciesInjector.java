package ru.xdark.telekinetic.di;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractDependenciesInjector implements DependenciesInjector {

    private final Map<Class<?>, Injector<?>> injectorsMap;
    private final Map<Class<?>, Injector<?>> view;

    protected AbstractDependenciesInjector(Map<Class<?>, Injector<?>> injectorsMap) {
        this.injectorsMap = injectorsMap;
        this.view = Collections.unmodifiableMap(injectorsMap);
    }

    protected AbstractDependenciesInjector(DependenciesInjector injector) {
        this(new HashMap<>(injector.injectors()));
    }

    protected AbstractDependenciesInjector() {
        this(new HashMap<>());
    }

    @Override
    public Map<Class<?>, Injector<?>> injectors() {
        return this.view;
    }

    @Override
    public <V> void registerInjector(Class<V> type, Injector<V> injector) {
        this.injectorsMap.put(
                Objects.requireNonNull(type, "type"),
                Objects.requireNonNull(injector, "injector")
        );
    }

    @Override
    public <V> Injector<V> getInjector(Class<V> type) {
        return (Injector<V>) this.injectorsMap.get(type);
    }
}
