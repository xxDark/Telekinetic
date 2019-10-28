package ru.xdark.modloader.minecraft.tweakers;

import lombok.val;
import ru.xdark.launcher.LauncherInitializationContext;
import ru.xdark.launcher.Tweaker;
import ru.xdark.modloader.Modloader;
import ru.xdark.modloader.minecraft.transformers.ClientBrandRetrieverTransformer;

public final class ClientTweaker implements Tweaker {
    @Override
    public void inject(LauncherInitializationContext context) {
        val launcher = context.getLauncher();
       launcher.registerTransformer(new ClientBrandRetrieverTransformer(Modloader.instance().loaderVersion()));
    }

    @Override
    public int getTweakOrder() {
        return 1000;
    }
}
