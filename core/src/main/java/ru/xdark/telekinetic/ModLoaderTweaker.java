package ru.xdark.telekinetic;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import ru.xdark.launcher.LauncherInitializationContext;
import ru.xdark.launcher.Tweaker;
import ru.xdark.telekinetic.environment.EnvironmentContext;
import ru.xdark.telekinetic.mod.ClassPathModsLocator;
import ru.xdark.telekinetic.mod.ModContainer;
import ru.xdark.telekinetic.mod.ModsLocateContext;
import ru.xdark.telekinetic.mod.ModsLocator;

import java.util.ArrayList;

@Log4j2
@RequiredArgsConstructor
final class ModLoaderTweaker implements Tweaker {

    private final ModLoader modLoader;

    @Override
    public void inject(LauncherInitializationContext context) {
        log.info("ModLoader injection");
        val launcher = context.getLauncher();
        val modLoader = this.modLoader;
        val mods = new ArrayList<ModContainer>();
        val primaryContainer = new ModLoaderContainer(modLoader);
        mods.add(primaryContainer);
        val locators = new ModsLocator[]{
                new ClassPathModsLocator()
        };
        val classLoader = context.getClassLoader();
        val locateContext = new ModsLocateContext(
                launcher,
                classLoader,
                modLoader
        );
        for (val locator : locators) mods.addAll(locator.findContainers(locateContext));
        log.info("Found {} mod(s)", mods.size());
        modLoader.setup(new EnvironmentContext(
                primaryContainer.getModDescription().getVersion(),
                classLoader,
                context.getGameDirectory(),
                mods,
                launcher.getProperty("side", DefaultSides.UNKNOWN)
        ));
    }

    @Override
    public int getTweakOrder() {
        return Integer.MAX_VALUE - 4; // reserve 4 values if someone wants to be first :x
    }
}
