package me.xdark.launcher;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Data
public final class LauncherInitializationContext {
    private final Path gameDirectory;
    private final Path assetsDirectory;
    private final String version;
}
