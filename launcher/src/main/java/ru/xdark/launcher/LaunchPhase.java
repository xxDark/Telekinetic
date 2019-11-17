package ru.xdark.launcher;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LaunchPhase {

    private final LauncherInitializationContext ctx;
    private final PhaseType type;

    static LaunchPhase makePhase(LauncherInitializationContext ctx, PhaseType type) {
        return new LaunchPhase(ctx, type);
    }

    public enum PhaseType {
        PRE_INITIALIZATION, //During this phase, LauncherInitializationContext is not ready yet
        INITIALIZATION,
        ABOUT_TO_START,
        START
    }
}
