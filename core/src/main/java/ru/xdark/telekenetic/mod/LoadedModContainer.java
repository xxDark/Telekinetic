package ru.xdark.telekenetic.mod;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.xdark.telekenetic.TelekineticModLoader;
import ru.xdark.telekenetic.resources.Resource;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class LoadedModContainer implements ModContainer {

    @Getter
    private final ModDescription modDescription;
    @Getter
    private final Object instance;

    @Override
    public List<Resource> findResources(String path) {
        return TelekineticModLoader.instance().getResourceManager().findResources(this, path);
    }

    @Override
    public boolean hasResource(String path) {
        return TelekineticModLoader.instance().getResourceManager().hasResource(this, path);
    }
}
