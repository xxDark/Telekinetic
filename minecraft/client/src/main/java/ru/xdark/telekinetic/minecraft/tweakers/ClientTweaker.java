package ru.xdark.telekinetic.minecraft.tweakers;

import joptsimple.OptionParser;
import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.xdark.launcher.LauncherInitializationContext;
import ru.xdark.launcher.Tweaker;
import ru.xdark.telekinetic.ModLoader;
import ru.xdark.telekinetic.minecraft.transformers.ClientBrandRetrieverTransformer;

@RequiredArgsConstructor
public final class ClientTweaker implements Tweaker {

    private final ModLoader modloader;

    @Override
    public void inject(LauncherInitializationContext context) {
        val parser = new OptionParser();
        val launcher = context.getLauncher();
       launcher.registerTransformer(new ClientBrandRetrieverTransformer(modloader.getVersion()));
    }

    @Override
    public int getTweakOrder() {
        return 1000;
    }
}
