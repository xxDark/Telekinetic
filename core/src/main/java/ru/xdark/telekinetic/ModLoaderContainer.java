package ru.xdark.telekinetic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.xdark.telekinetic.mod.Mod;
import ru.xdark.telekinetic.mod.ModContainer;
import ru.xdark.telekinetic.mod.ModDescription;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ModLoaderContainer implements ModContainer {

    @Getter
    private final ModLoader instance;
    @Getter
    private final ModDescription modDescription = ModDescription.fromAnnotation(ModLoader.class.getAnnotation(Mod.class));
}
