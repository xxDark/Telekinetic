package ru.xdark.launcher;

import java.util.Set;

public interface Launcher {

    void addClassLoadingExclusions(String... exclusions);

    void addClassLoadingExclusion(String exclusion);

    void addTransformerExclusions(String... exclusions);

    void addTransformerExclusion(String exclusion);

    void registerTransformer(ClassFileTransformer transformer);

    void registerTweaker(Tweaker tweaker);

    void injectTweakers(LauncherInitializationContext context);

    void setLaunchTarget(String target);

    String getLaunchTarget();

    ClassTransformation runTransformation(ClassTransformation transformation);

    String transformClassName(String className);

    String untransformClassName(String className);

    boolean isClassLoadingExclusion(String className);

    boolean isTransformationExclusion(String className);

    Set<LauncherOption> getLauncherOptions();

    boolean isOptionSet(LauncherOption option);

    default void gotoPhase(LaunchPhase phase) { }
}
