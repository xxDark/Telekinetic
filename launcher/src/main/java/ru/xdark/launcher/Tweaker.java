package ru.xdark.launcher;

import java.util.List;

public interface Tweaker extends Comparable<Tweaker> {

    void inject(LauncherInitializationContext context);

    void injectArguments(List<String> arguments);

    int getTweakOrder();

    @Override
    default int compareTo(Tweaker tweaker) {
        return Integer.compare(getTweakOrder(), tweaker.getTweakOrder());
    }
}
