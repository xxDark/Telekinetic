package ru.xdark.telekinetic.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Log4j2
@RequiredArgsConstructor
public final class SimpleEventBus implements EventBus {

    private final Map<Class<? extends Event>, List<RegisteredListener>> executorsMap = new HashMap<>();
    private final Map<Object, Set<RegisteredListener>> byMods = new HashMap<>();
    private final ClassLoader loader;

    @Override
    public RegisteredListener registerListeners(Object mod, Object listener) {
        BusRegisteredListener registeredListener = null;
        for (val m : listener.getClass().getDeclaredMethods()) {
            val annotation = m.getDeclaredAnnotation(Listener.class);
            if (annotation == null) continue;
            val priority = annotation.priority();
            log.debug("Found {} w/ @Listener marker, priority: {}", m, priority);
            val types = m.getParameterTypes();
            if (types.length != 1) {
                throw new IllegalStateException("Expected one argument for the listener, but got: " + m.getParameterCount());
            }
            val event = types[0];
            if (!Event.class.isAssignableFrom(event)) {
                throw new IllegalStateException("First argument is not an instance of Event class!");
            }
            log.debug("Generating event executor for {}", m);
            val eventExecutor = ASMEventExecutor.generateExecutor(listener, m, loader);
            log.debug("Generated event executor for {}", m);
            val asEvent = (Class<? extends Event>) event;
            val handle = new BusRegisteredListener(asEvent, mod, eventExecutor, this, priority);
            if (registeredListener == null) {
                registeredListener = handle;
                log.debug("Primary listener is: {}", handle);
            } else {
                registeredListener.attached = handle;
                log.debug("Attached {} to {}", handle, registeredListener);
                registeredListener = handle;
            }
            registerListener(asEvent, mod, handle);
        }
        return registeredListener;
    }

    @Override
    public <T extends Event> RegisteredListener registerListener(Object mod, Class<T> eventClass, EventExecutor<T> executor, int priority) {
        val listener = new BusRegisteredListener(eventClass, mod, executor, this, priority);
        registerListener(eventClass, mod, listener);
        return listener;
    }

    @Override
    public boolean unregisterListener(Object mod, EventExecutor<?> executor) {
        val byMod = byMods.get(mod);
        if (byMod == null) return false;
        val removedAny = byMod.removeIf(listener -> {
            if (listener.getExecutor() == executor) {
                val eventClass = listener.getEventClass();
                val executors = executorsMap.get(eventClass);
                if (executors != null && executors.remove(listener)) {
                    if (!executors.isEmpty()) {
                        executors.sort(Comparable::compareTo);
                    } else {
                        executorsMap.remove(eventClass);
                    }
                }
                return true;
            }
            return false;
        });
        if (byMod.isEmpty()) {
            byMods.remove(mod);
        }
        return removedAny;
    }

    @Override
    public boolean unregisterListeners(Object mod) {
        val listeners = byMods.remove(mod);
        if (listeners == null) return false;
        boolean removedAny = false;
        for (val listener : listeners) {
            val eventClass = listener.getEventClass();
            val eventListeners = executorsMap.get(eventClass);
            if (eventListeners == null) continue;
            if (eventListeners.remove(listener)) {
                removedAny = true;
                if (eventListeners.isEmpty()) {
                    executorsMap.remove(eventClass);
                } else {
                    eventListeners.sort(Comparable::compareTo);
                }
            }
        }
        return removedAny;
    }

    @Override
    public boolean unregisterListeners(Object mod, Class<? extends Event> eventClass) {
        val executors = executorsMap.get(eventClass);
        if (executors == null) return false;
        val modListeners = byMods.get(mod);
        if (modListeners == null) return false;
        val unregistered = modListeners.removeIf(listener -> {
            if (listener.getEventClass() == eventClass) {
                if (executors.remove(listener) && executors.isEmpty()) {
                    executorsMap.remove(eventClass);
                }
                return true;
            }
            return false;
        });
        if (modListeners.isEmpty()) byMods.remove(mod);
        if (!executors.isEmpty()) {
            executors.sort(Comparable::compareTo);
        } else executorsMap.remove(eventClass);
        return unregistered;
    }

    @Override
    public <T extends Event> CompletableFuture<T> dispatch(T event, Executor executor) {
        if (event == null) return CompletableFuture.completedFuture(null);
        val listeners = executorsMap.get(event.getClass());
        if (listeners == null) return CompletableFuture.completedFuture(event);
        val future = new CompletableFuture<T>();
        executor.execute(() -> {
            try {
                listeners.forEach(listener -> listener.getExecutor().dispatch(event));
                future.complete(event);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    @Override
    public <T extends Event> void fireAndForget(T event, Executor executor) {
        if (event == null) return;
        val listeners = executorsMap.get(event.getClass());
        if (listeners == null) return;
        executor.execute(() -> listeners.forEach(listener -> listener.getExecutor().dispatch(event)));
    }

    @Override
    public boolean isListenerRegistered(Object mod, EventExecutor<?> executor) {
        val byMod = byMods.get(mod);
        return byMod != null && byMod.stream().anyMatch(listener -> listener.getExecutor() == executor);
    }

    private void registerListener(Class<? extends Event> event, Object mod, RegisteredListener listener) {
        val executors = executorsMap.computeIfAbsent(event, __ -> new ArrayList<>(4));
        executors.add(listener);
        executors.sort(Comparable::compareTo);
        byMods.computeIfAbsent(mod, __ -> new HashSet<>(4)).add(listener);
    }
}
