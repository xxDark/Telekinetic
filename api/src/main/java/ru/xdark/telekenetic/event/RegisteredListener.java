package ru.xdark.telekenetic.event;

import ru.xdark.telekenetic.EventExecutor;

public interface RegisteredListener extends Comparable<RegisteredListener> {

    Class<? extends Event> getEventClass();

    Object getOwner();

    EventBus getBus();

    EventExecutor getExecutor();

    int getPriority();

    boolean isRegistered();

    boolean unregister();
}
