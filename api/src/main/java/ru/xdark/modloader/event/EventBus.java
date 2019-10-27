package ru.xdark.modloader.event;

import ru.xdark.modloader.EventExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface EventBus {

    RegisteredListener registerListeners(Object mod);

    <T extends Event> RegisteredListener registerListener(Object mod, Class<T> eventClass, EventExecutor<T> executor, int priority);

    void unregisterListener(Object mod, EventExecutor<?> executor);

    void unregisterListeners(Object mod);

    void unregisterListeners(Object mod, Class<? extends Event> eventClass);

    <T extends Event> CompletableFuture<T> dispatch(T event, Executor executor);

    <T extends Event> void fireAndForget(T event);

    boolean isListenerRegistered(Object mod, EventExecutor<?> executor);
}
