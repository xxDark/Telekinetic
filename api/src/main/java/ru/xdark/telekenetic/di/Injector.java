package ru.xdark.telekenetic.di;

@FunctionalInterface
public interface Injector<V> {
    V inject(Object instance) throws Exception;
}
