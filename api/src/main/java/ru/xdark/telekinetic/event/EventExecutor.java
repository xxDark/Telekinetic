package ru.xdark.telekinetic.event;

public interface EventExecutor<T extends Event> {

    void dispatch(T event);
}
