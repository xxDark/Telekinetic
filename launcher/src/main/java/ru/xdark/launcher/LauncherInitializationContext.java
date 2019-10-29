package ru.xdark.launcher;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Data
public final class LauncherInitializationContext {
    private final Launcher launcher;
    private final LauncherClassLoader classLoader;
    private final List<String> arguments;
    private final Path gameDirectory;
}
