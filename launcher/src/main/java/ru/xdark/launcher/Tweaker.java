package ru.xdark.launcher;

public interface Tweaker extends Comparable<Tweaker> {

    void inject(LauncherInitializationContext context);

    int getTweakOrder();

    @Override
    default int compareTo(Tweaker tweaker) {
        return Integer.compare(getTweakOrder(), tweaker.getTweakOrder());
    }
}
