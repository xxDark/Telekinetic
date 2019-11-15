package org.spongepowered.asm.launch;

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
    public void inject(LauncherInitializationContext context) {
        MixinBootstrap.start();
        MixinBootstrap.doInit(CommandLineOptions.ofArgs(context.getArguments()));
        MixinBootstrap.inject();
    }

    @Override
    public int getTweakOrder() {
        return Integer.MAX_VALUE - 15;
    }

    @Override
    public void gotoPhase(LaunchPhase phase) {
        if (phase == LaunchPhase.ABOUT_TO_START) {
            EnvironmentBridge.gotoDefaultPhase();
        }
    }
}
