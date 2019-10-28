package ru.xdark.telekenetic.minecraft;

import lombok.val;
import ru.xdark.launcher.Launcher;
import ru.xdark.launcher.LauncherBootstrapper;
import ru.xdark.telekenetic.TelekineticModLoader;

public final class ServerLauncherBootstrapper implements LauncherBootstrapper {

    @Override
    public Launcher create() {
        val launcher = new ServerApplicationLauncher();
        launcher.setLaunchTarget("net.minecraft.server.MinecraftServer");
        TelekineticModLoader.instance().inject(launcher);
        return launcher;
    }
}
