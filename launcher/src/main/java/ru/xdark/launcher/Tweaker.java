package ru.xdark.launcher;

public interface Tweaker extends Comparable<Tweaker> {

    void inject(LauncherInitializationContext context);

    int getTweakOrder();

    default void gotoPhase(LaunchPhase phase) { }

    @Override
    default int compareTo(Tweaker tweaker) {
        return Integer.compare(tweaker.getTweakOrder(), getTweakOrder());
    }
}
