package ru.xdark.modloader.environment;

import lombok.RequiredArgsConstructor;
import ru.xdark.modloader.mod.ModContainer;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public final class EnvironmentContext {
    final ClassLoader loader;
    final Path workingDirectory;
    final List<ModContainer> loadedMods;
}
