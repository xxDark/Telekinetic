package ru.xdark.telekenetic.event;

import ru.xdark.telekenetic.EventExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface EventBus {

    RegisteredListener registerListeners(Object mod);

    <T extends Event> RegisteredListener registerListener(Object mod, Class<T> eventClass, EventExecutor<T> executor, int priority);

    boolean unregisterListener(Object mod, EventExecutor<?> executor);

    boolean unregisterListeners(Object mod);

    boolean unregisterListeners(Object mod, Class<? extends Event> eventClass);

    <T extends Event> CompletableFuture<T> dispatch(T event, Executor executor);

    <T extends Event> void fireAndForget(T event, Executor executor);

    boolean isListenerRegistered(Object mod, EventExecutor<?> executor);
}
