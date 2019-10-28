package ru.xdark.telekenetic.environment;

import lombok.RequiredArgsConstructor;
import ru.xdark.telekenetic.mod.ModContainer;
import ru.xdark.telekenetic.version.Version;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public final class EnvironmentContext {
    final Version loaderVersion;
    final ClassLoader loader;
    final Path workingDirectory;
    final List<ModContainer> loadedMods;
}
