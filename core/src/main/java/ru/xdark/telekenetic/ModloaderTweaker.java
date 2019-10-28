package ru.xdark.telekenetic;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import ru.xdark.launcher.LauncherInitializationContext;
import ru.xdark.launcher.Tweaker;
import ru.xdark.telekenetic.environment.EnvironmentContext;
import ru.xdark.telekenetic.mod.ClassPathModsLocator;
import ru.xdark.telekenetic.mod.ModContainer;
import ru.xdark.telekenetic.mod.ModsLocateContext;
import ru.xdark.telekenetic.mod.ModsLocator;

import java.util.ArrayList;

@Log4j2
@RequiredArgsConstructor
final class ModloaderTweaker implements Tweaker {

    private final ModLoader modloader;

    @Override
    public void inject(LauncherInitializationContext context) {
        log.info("Modloader injection");
        val modLoader = this.modloader;
        val mods = new ArrayList<ModContainer>();
        val primaryContainer = new ModLoaderContainer(modLoader);
        mods.add(primaryContainer);
        val locators = new ModsLocator[]{
                new ClassPathModsLocator()
        };
        val launcher = context.getLauncher();
        val classLoader = context.getClassLoader();
        val locateContext = new ModsLocateContext(
                launcher,
                classLoader,
                modLoader
        );
        for (val locator : locators) mods.addAll(locator.findContainers(locateContext));
        log.info("Found {} total mods", mods.size());
        modLoader.setup(new EnvironmentContext(
                primaryContainer.getModDescription().getVersion(),
                classLoader,
                context.getGameDirectory(),
                mods
        ));
    }

    @Override
    public int getTweakOrder() {
        return Integer.MAX_VALUE - 16; // reserve 16 values if someone wants to be first :x
    }
}
