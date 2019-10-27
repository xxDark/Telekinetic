package ru.xdark.modloader.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.xdark.modloader.EventExecutor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Data
final class BusRegisteredListener implements RegisteredListener {

    private final Object owner;
    private final EventExecutor<?> executor;
    private final EventBus bus;

    @Override
    public boolean isRegistered() {
        return bus.isListenerRegistered(owner, executor);
    }

    @Override
    public boolean unregister() {
        if (isRegistered()) {
            bus.unregisterListener(owner, executor);
            return true;
        }
        return false;
    }
}
