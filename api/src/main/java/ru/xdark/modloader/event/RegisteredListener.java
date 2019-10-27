package ru.xdark.modloader.event;

public interface RegisteredListener {

    Object getOwner();

    EventBus getBus();

    boolean isRegistered();

    boolean unregister();
}
