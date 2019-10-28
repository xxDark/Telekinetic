package ru.xdark.modloader.minecraft;

import lombok.val;
import ru.xdark.launcher.Launcher;
import ru.xdark.launcher.LauncherBootstrapper;
import ru.xdark.modloader.Modloader;

public final class ClientLauncherBootstrapper implements LauncherBootstrapper {

    @Override
    public Launcher create() {
        val launcher = new ClientApplicationLauncher();
        launcher.setLaunchTarget("net.minecraft.client.main.Main");
        Modloader.instance().inject(launcher);
        return launcher;
    }
}
