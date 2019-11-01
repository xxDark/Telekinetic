package ru.xdark.telekinetic.environment;

import lombok.RequiredArgsConstructor;
import ru.xdark.telekinetic.Side;
import ru.xdark.telekinetic.mod.ModContainer;
import ru.xdark.telekinetic.version.Version;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public final class EnvironmentContext {
    final Version loaderVersion;
    final ClassLoader loader;
    final Path workingDirectory;
    final List<ModContainer> loadedMods;
    final Side side;
}
