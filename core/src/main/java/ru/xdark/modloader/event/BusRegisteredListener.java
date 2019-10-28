package ru.xdark.modloader.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.xdark.modloader.EventExecutor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Data
final class BusRegisteredListener implements RegisteredListener {

    private final Class<? extends Event> eventClass;
    private final Object owner;
    private final EventExecutor<?> executor;
    private final EventBus bus;
    private final int priority;
    BusRegisteredListener attached;

    @Override
    public boolean isRegistered() {
        return bus.isListenerRegistered(owner, executor);
    }

    @Override
    public boolean unregister() {
        if (isRegistered()) {
            bus.unregisterListener(owner, executor);
            val attached = this.attached;
            if (attached != null) attached.unregister();
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(RegisteredListener o) {
        return Integer.compare(priority, o.getPriority());
    }
}
