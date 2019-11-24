package ru.xdark.telekinetic.mod;

import lombok.Getter;

@Getter
final class LoadedModContainer implements ModContainer {

    private final ModDescription modDescription;
    private final Object instance;

    LoadedModContainer(ModDescription modDescription, Object instance) {
        this.modDescription = modDescription;
        this.instance = instance;
    }
}
