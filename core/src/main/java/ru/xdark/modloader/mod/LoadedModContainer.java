package ru.xdark.modloader.mod;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.xdark.modloader.Modloader;
import ru.xdark.modloader.resources.Resource;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class LoadedModContainer implements ModContainer {

    @Getter
    private final ModDescription modDescription;
    @Getter
    private final Object instance;

    @Override
    public List<Resource> findResources(String path) {
        return Modloader.instance().resourceManager().findResources(this, path);
    }

    @Override
    public boolean hasResource(String path) {
        return Modloader.instance().resourceManager().hasResource(this, path);
    }
}
