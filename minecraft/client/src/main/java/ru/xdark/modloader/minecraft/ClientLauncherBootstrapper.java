package ru.xdark.modloader.minecraft;

import lombok.val;
import ru.xdark.launcher.Launcher;
import ru.xdark.launcher.LauncherBootstrapper;
import ru.xdark.modloader.Modloader;
import ru.xdark.modloader.minecraft.tweakers.ClientTweaker;

public final class ClientLauncherBootstrapper implements LauncherBootstrapper {

    @Override
    public Launcher create() {
        val launcher = new ClientApplicationLauncher();
        launcher.setLaunchTarget("net.minecraft.client.main.Main");
        val loader = Modloader.instance();
        loader.inject(launcher);
        launcher.registerTweaker(new ClientTweaker());
        return launcher;
    }
}
