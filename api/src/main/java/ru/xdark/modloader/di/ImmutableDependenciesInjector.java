package ru.xdark.modloader.di;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public final class ImmutableDependenciesInjector implements DependenciesInjector {

    private final DependenciesInjector injector;

    @Override
    public Map<Class<?>, Injector<?>> injectors() {
        return this.injector.injectors();
    }

    @Override
    public <V> void registerInjector(Class<V> type, Injector<V> injector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V> Injector<V> getInjector(Class<V> type) {
        return this.injector.getInjector(type);
    }

    @Override
    public int inject(Object o) {
        return this.injector.inject(o);
    }

    @Override
    public DependenciesInjector toImmutable() {
        return this;
    }
}
