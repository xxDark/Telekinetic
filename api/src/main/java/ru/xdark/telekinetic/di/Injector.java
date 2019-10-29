package ru.xdark.telekinetic.di;

@FunctionalInterface
public interface Injector<V> {
    V inject(Object instance) throws Exception;
}
