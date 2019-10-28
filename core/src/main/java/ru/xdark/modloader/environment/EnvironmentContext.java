package ru.xdark.modloader.environment;

import lombok.RequiredArgsConstructor;
import ru.xdark.modloader.mod.ModContainer;
import ru.xdark.modloader.version.Version;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public final class EnvironmentContext {
    final Version loaderVersion;
    final ClassLoader loader;
    final Path workingDirectory;
    final List<ModContainer> loadedMods;
}
