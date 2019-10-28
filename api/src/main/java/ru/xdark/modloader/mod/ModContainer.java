package ru.xdark.modloader.mod;

import ru.xdark.modloader.resources.ResourcesContainer;

public interface ModContainer extends ResourcesContainer {

    ModDescription getModDescription();

    Object getInstance();
}
