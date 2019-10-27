package ru.xdark.modloader.envinornment;

import ru.xdark.modloader.event.EventBus;
import ru.xdark.modloader.resources.ResourceManager;

public interface ModsEnvironment {

    ResourceManager getResourceManager();

    EventBus getEventBus();
}
