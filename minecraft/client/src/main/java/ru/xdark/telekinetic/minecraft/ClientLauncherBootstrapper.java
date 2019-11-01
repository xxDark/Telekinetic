package ru.xdark.telekinetic.minecraft;

import lombok.val;
import ru.xdark.launcher.Launcher;
import ru.xdark.telekinetic.DefaultSides;
import ru.xdark.telekinetic.launcher.TelekineticLauncherBootstrapper;

public final class ClientLauncherBootstrapper extends TelekineticLauncherBootstrapper {

    @Override
    public Launcher create() {
        val launcher = new ClientApplicationLauncher();
        launcher.setLaunchTarget("net.minecraft.client.main.Main");
        launcher.getProperties().put("side", DefaultSides.CLIENT);
        inject(launcher);
        //launcher.registerTweaker(new ClientTweaker(loader));
        return launcher;
    }
}
