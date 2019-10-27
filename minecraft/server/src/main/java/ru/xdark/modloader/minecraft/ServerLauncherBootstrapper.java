package ru.xdark.modloader.minecraft;

import lombok.val;
import ru.xdark.launcher.Launcher;
import ru.xdark.launcher.LauncherBootstrapper;

public final class ServerLauncherBootstrapper implements LauncherBootstrapper {

    @Override
    public Launcher create() {
        val launcher = new ServerApplicationLauncher();
        launcher.setLaunchTarget("net.minecraft.server.MinecraftServer");
        return launcher;
    }
}