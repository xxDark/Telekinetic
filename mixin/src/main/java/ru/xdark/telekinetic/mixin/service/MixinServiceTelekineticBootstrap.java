package ru.xdark.telekinetic.mixin.service;

import org.spongepowered.asm.service.IMixinServiceBootstrap;

public final class MixinServiceTelekineticBootstrap implements IMixinServiceBootstrap {
    @Override
    public String getName() {
        return "Telekinetic";
    }

    @Override
    public String getServiceClassName() {
        return "ru.xdark.telekinetic.mixin.service.MixinServiceTelekinetic";
    }

    @Override
    public void bootstrap() { }
}
