package ru.xdark.modloader;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import ru.xdark.launcher.LauncherInitializationContext;
import ru.xdark.launcher.Tweaker;
import ru.xdark.modloader.environment.EnvironmentContext;
import ru.xdark.modloader.mod.ClassPathModsLocator;
import ru.xdark.modloader.mod.ModContainer;
import ru.xdark.modloader.mod.ModsLocator;

import java.util.ArrayList;

@Log4j2
@RequiredArgsConstructor
final class ModloaderTweaker implements Tweaker {

    private final Modloader modloader;

    @Override
    public void inject(LauncherInitializationContext context) {
        log.info("Modloader injection");
        val modloader = this.modloader;
        val mods = new ArrayList<ModContainer>();
        mods.add(new ModloaderContainer(modloader));
        val locators = new ModsLocator[]{
                new ClassPathModsLocator()
        };
        val launcher = context.getLauncher();
        val classLoader = context.getClassLoader();
        for (val locator : locators) mods.addAll(locator.findContainers(launcher, classLoader));
        log.info("Found {} total mods", mods.size());
        val envCtx = new EnvironmentContext(classLoader, context.getGameDirectory(), mods);
        log.info("Bootstrapping Modloader");
        modloader.setup(envCtx);
    }

    @Override
    public int getTweakOrder() {
        return Integer.MAX_VALUE - 16; // reserve 16 values if someone wants to be first :x
    }
}
