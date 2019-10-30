package ru.xdark.telekinetic.mixin.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.spongepowered.asm.service.ITransformer;
import ru.xdark.launcher.ClassFileTransformer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class TelekineticTransformerWrapper implements ITransformer {

    private final ClassFileTransformer handle;

    @Override
    public String getName() {
        return handle.getClass().getName();
    }

    @Override
    public boolean isDelegationExcluded() {
        return false;
    }
}
