package ru.xdark.telekinetic.mod;

import ru.xdark.telekinetic.resources.ResourcesContainer;

public interface ModContainer extends ResourcesContainer {

    ModDescription getModDescription();

    Object getInstance();
}
