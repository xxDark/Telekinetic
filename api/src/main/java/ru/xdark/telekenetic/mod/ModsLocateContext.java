package ru.xdark.telekenetic.mod;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.xdark.launcher.Launcher;
import ru.xdark.launcher.LauncherClassLoader;
import ru.xdark.telekenetic.ModLoader;

@RequiredArgsConstructor
@Data
public final class ModsLocateContext {
    private final Launcher launcher;
    private final LauncherClassLoader classLoader;
    private final ModLoader modloader;
}
