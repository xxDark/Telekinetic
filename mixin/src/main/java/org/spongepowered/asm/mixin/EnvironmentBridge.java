package org.spongepowered.asm.mixin;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnvironmentBridge {

    public void gotoDefaultPhase() {
        MixinEnvironment.gotoPhase(MixinEnvironment.Phase.DEFAULT);
    }
}
