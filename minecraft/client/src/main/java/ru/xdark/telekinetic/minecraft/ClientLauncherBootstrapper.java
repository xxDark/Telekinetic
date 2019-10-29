package ru.xdark.telekinetic.minecraft;

import lombok.val;
import ru.xdark.launcher.Launcher;
import ru.xdark.launcher.LauncherBootstrapper;
import ru.xdark.telekinetic.TelekineticModLoader;
import ru.xdark.telekinetic.minecraft.tweakers.ClientTweaker;

public final class ClientLauncherBootstrapper implements LauncherBootstrapper {

    @Override
    public Launcher create() {
        val launcher = new ClientApplicationLauncher();
        launcher.setLaunchTarget("net.minecraft.client.main.Main");
        val loader = TelekineticModLoader.instance();
        loader.inject(launcher);
        launcher.registerTweaker(new ClientTweaker(loader));
        return launcher;
    }
}
