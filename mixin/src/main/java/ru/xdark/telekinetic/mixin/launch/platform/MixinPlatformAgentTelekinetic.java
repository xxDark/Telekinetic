package ru.xdark.telekinetic.mixin.launch.platform;

import org.spongepowered.asm.launch.platform.IMixinPlatformServiceAgent;
import org.spongepowered.asm.launch.platform.MixinPlatformAgentAbstract;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import ru.xdark.launcher.RootLauncher;
import ru.xdark.telekinetic.DefaultSides;

import java.util.Collection;

public final class MixinPlatformAgentTelekinetic extends MixinPlatformAgentAbstract implements IMixinPlatformServiceAgent {
    @Override
    public void init() { }

    @Override
    public String getSideName() {
        return RootLauncher.get().getProperty("side", DefaultSides.UNKNOWN).getId();
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return null;
    }
}
