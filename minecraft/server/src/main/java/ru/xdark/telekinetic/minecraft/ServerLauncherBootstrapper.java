package ru.xdark.telekinetic.minecraft;

import lombok.val;
import ru.xdark.launcher.Launcher;
import ru.xdark.telekinetic.DefaultSides;
import ru.xdark.telekinetic.launcher.TelekineticLauncherBootstrapper;

public final class ServerLauncherBootstrapper extends TelekineticLauncherBootstrapper {

    @Override
    public Launcher create() {
        val launcher = new ServerApplicationLauncher();
        launcher.setLaunchTarget("net.minecraft.server.MinecraftServer");
        launcher.getProperties().put("side", DefaultSides.SERVER);
        inject(launcher);
        return launcher;
    }
}
