package ru.xdark.telekenetic;

import ru.xdark.telekenetic.event.Event;

@FunctionalInterface
public interface EventExecutor<T extends Event> {

    void dispatch(T event);
}
