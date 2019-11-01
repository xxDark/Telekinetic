package ru.xdark.launcher;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RootLauncher {
    private Launcher rootLauncher;

    void set(Launcher launcher) {
        RootLauncher.rootLauncher = launcher;
    }

    public Launcher get() {
        return rootLauncher;
    }
}
