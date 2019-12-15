package org.spongepowered.telekinetic;

import org.spongepowered.asm.service.IMixinAuditTrail;

final class TelekineticAuditTail implements IMixinAuditTrail {
    @Override
    public void onApply(String className, String mixinName) {
        // Do nothing
    }

    @Override
    public void onPostProcess(String className) {
        // Do nothing
    }

    @Override
    public void onGenerate(String className, String generatorName) {
        // Do nothing
    }
}
