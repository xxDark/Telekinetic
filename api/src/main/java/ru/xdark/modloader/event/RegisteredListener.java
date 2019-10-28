package ru.xdark.modloader.event;

import ru.xdark.modloader.EventExecutor;

public interface RegisteredListener extends Comparable<RegisteredListener> {

    Class<? extends Event> getEventClass();

    Object getOwner();

    EventBus getBus();

    EventExecutor getExecutor();

    int getPriority();

    boolean isRegistered();

    boolean unregister();
}
