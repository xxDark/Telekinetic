package ru.xdark.telekenetic.mod;

import ru.xdark.telekenetic.resources.ResourcesContainer;

public interface ModContainer extends ResourcesContainer {

    ModDescription getModDescription();

    Object getInstance();
}
