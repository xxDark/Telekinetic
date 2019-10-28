package ru.xdark.modloader.mod;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import ru.xdark.modloader.Identifiable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ModContainer implements Identifiable<String> {

    private final Object instance;
    @Delegate(types = Mod.class)
    private final Mod information;

    @Override
    public String getId() {
        return this.information.id();
    }

    public Object instance() {
        return this.instance;
    }
}
