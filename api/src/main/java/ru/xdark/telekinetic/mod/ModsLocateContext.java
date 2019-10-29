package ru.xdark.telekinetic.mod;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.xdark.launcher.Launcher;
import ru.xdark.launcher.LauncherClassLoader;
import ru.xdark.telekinetic.ModLoader;

@RequiredArgsConstructor
@Data
public final class ModsLocateContext {
    private final Launcher launcher;
    private final LauncherClassLoader classLoader;
    private final ModLoader modLoader;
}
