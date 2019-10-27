package ru.xdark.modloader;

import ru.xdark.modloader.event.Event;

@FunctionalInterface
public interface EventExecutor<T extends Event> {

    void dispatch(T event);
}
