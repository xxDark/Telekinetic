package ru.xdark.telekinetic.launcher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.xdark.launcher.Launcher;
import ru.xdark.launcher.LauncherBootstrapper;
import ru.xdark.telekinetic.TelekineticModLoader;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TelekineticLauncherBootstrapper implements LauncherBootstrapper {

    protected final void inject(Launcher launcher) {
        TelekineticModLoader.instance().inject(launcher);
    }
}
