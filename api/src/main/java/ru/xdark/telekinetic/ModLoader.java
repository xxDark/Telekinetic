package ru.xdark.telekinetic;

import ru.xdark.launcher.Launcher;
import ru.xdark.telekinetic.environment.EnvironmentContext;
import ru.xdark.telekinetic.environment.ModsEnvironment;
import ru.xdark.telekinetic.mod.Mod;

@Mod(
        name = "Telekinetic",
        id = "modLoader",
        version = "1.1.4-BETA",
        authors = "__xDark"
)
public interface ModLoader extends ModsEnvironment {

    void inject(Launcher launcher);

    void setup(EnvironmentContext ctx);

    ModsEnvironment getEnvironment();
}
