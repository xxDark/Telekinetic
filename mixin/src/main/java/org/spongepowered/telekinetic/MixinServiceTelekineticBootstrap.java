package org.spongepowered.telekinetic;

import org.spongepowered.asm.service.IMixinServiceBootstrap;

public final class MixinServiceTelekineticBootstrap implements IMixinServiceBootstrap {
    @Override
    public String getName() {
        return "Telekinetic";
    }

    @Override
    public String getServiceClassName() {
        return "org.spongepowered.telekinetic.MixinServiceTelekinetic";
    }

    @Override
    public void bootstrap() { }
}
