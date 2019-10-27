package ru.xdark.modloader.minecraft;

import ru.xdark.launcher.ApplicationLauncher;
import ru.xdark.launcher.LauncherOption;

import java.util.Collections;
import java.util.Set;

public final class ServerApplicationLauncher extends ApplicationLauncher {
    @Override
    public Set<LauncherOption> getLauncherOptions() {
        return Collections.emptySet();
    }
}
