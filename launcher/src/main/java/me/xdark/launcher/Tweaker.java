package me.xdark.launcher;

import java.util.List;

public interface Tweaker extends Comparable<Tweaker> {

    void prepare(LauncherInitializationContext context);

    void injectArguments(List<String> arguments);

    void injectIntoLauncher(Launcher launcher);

    int getTweakOrder();

    @Override
    default int compareTo(Tweaker tweaker) {
        return Integer.compare(getTweakOrder(), tweaker.getTweakOrder());
    }
}
