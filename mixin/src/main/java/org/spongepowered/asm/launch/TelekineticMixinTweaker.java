package org.spongepowered.asm.launch;

import lombok.val;
import org.spongepowered.asm.launch.platform.CommandLineOptions;
import org.spongepowered.asm.mixin.EnvironmentBridge;
import ru.xdark.launcher.LaunchPhase;
import ru.xdark.launcher.LauncherInitializationContext;
import ru.xdark.launcher.Tweaker;

/**
 * Modified version of original tweaker that works with
 * Telekinetic mod loader
 */
public final class TelekineticMixinTweaker implements Tweaker {

    @Override
    public void inject(LauncherInitializationContext ctx) {
        val launcher = ctx.getLauncher();
        launcher.addTransformerExclusion("org.spongepowered.asm.");
        MixinBootstrap.start();
        MixinBootstrap.doInit(CommandLineOptions.ofArgs(ctx.getArguments()));
        MixinBootstrap.inject();
    }

    @Override
    public int getTweakOrder() {
        return Integer.MAX_VALUE - 15;
    }

    @Override
    public void gotoPhase(LaunchPhase phase) {
        if (phase.getType() == LaunchPhase.PhaseType.ABOUT_TO_START) {
            EnvironmentBridge.gotoDefaultPhase();
        }
    }
}
