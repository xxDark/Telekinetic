package org.spongepowered.asm.launch;

import org.spongepowered.asm.launch.platform.CommandLineOptions;
import ru.xdark.launcher.LauncherInitializationContext;
import ru.xdark.launcher.Tweaker;

/**
 * Modified version of original tweaker that works with
 * Telekinetic mod loader
 */
public final class TelekineticMixinTweaker implements Tweaker {

    @Override
    public void inject(LauncherInitializationContext context) {
        context.getLauncher().addClassLoadingExclusion("org.spongepowered.");
        MixinBootstrap.start();
        MixinBootstrap.doInit(CommandLineOptions.ofArgs(context.getArguments()));
        MixinBootstrap.inject();
    }

    @Override
    public int getTweakOrder() {
        return Integer.MAX_VALUE - 17;
    }
}
