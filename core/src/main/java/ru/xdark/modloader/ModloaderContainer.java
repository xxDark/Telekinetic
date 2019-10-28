package ru.xdark.modloader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.xdark.modloader.mod.Mod;
import ru.xdark.modloader.mod.ModContainer;
import ru.xdark.modloader.mod.ModDescription;
import ru.xdark.modloader.resources.Resource;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ModloaderContainer implements ModContainer {

    @Getter
    private final Modloader instance;
    @Getter
    private final ModDescription modDescription = ModDescription.fromAnnotation(Modloader.class.getAnnotation(Mod.class));

    @Override
    public List<Resource> findResources(String path) {
        return instance.findResources(path);
    }

    @Override
    public boolean hasResource(String path) {
        return instance.hasResource(path);
    }
}
