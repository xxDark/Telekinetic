package ru.xdark.telekenetic.di;

import java.util.Map;

public interface DependenciesInjector {

    Map<Class<?>, Injector<?>> injectors();

    <V> void registerInjector(Class<V> type, Injector<V> injector);

    <V> Injector<V> getInjector(Class<V> type);

    int inject(Object o);

    DependenciesInjector toImmutable();
}
