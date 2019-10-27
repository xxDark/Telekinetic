package ru.xdark.modloader.di;

@FunctionalInterface
public interface Injector<V> {
    V inject(Object instance) throws Exception;
}
