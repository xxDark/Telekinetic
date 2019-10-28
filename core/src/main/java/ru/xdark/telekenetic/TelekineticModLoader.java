package ru.xdark.telekenetic;

import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.val;
import ru.xdark.launcher.Launcher;
import ru.xdark.telekenetic.environment.DefaultModsEnvironment;
import ru.xdark.telekenetic.environment.EnvironmentContext;
import ru.xdark.telekenetic.environment.ModsEnvironment;

public final class TelekineticModLoader implements ModLoader {

    private static final ModLoader instance = new TelekineticModLoader();
    @Delegate(types = ModsEnvironment.class)
    @Getter
    private ModsEnvironment environment;

    @Override
    public void inject(Launcher launcher) {
        launcher.registerTweaker(new ModLoaderTweaker(this));
    }

    @Override
    public void setup(EnvironmentContext context) {
        val environment = this.environment = new DefaultModsEnvironment(context);
        val bus = environment.getEventBus();
        val injector = environment.getDependenciesInjector();
        for (val mod : environment.getLoadedMods()) {
            val instance = mod.getInstance();
            bus.registerListeners(instance);
            injector.inject(instance);
        }
    }

    public static ModLoader instance() {
        return instance;
    }
}
