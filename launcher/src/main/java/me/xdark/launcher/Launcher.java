package me.xdark.launcher;

import java.util.Set;

public interface Launcher {

    void addClassLoadingExclusions(String... exclusions);

    void addTransformerExclusions(String... exclusions);

    void registerTransformer(ClassFileTransformer transformer);

    void registerTweaker(Tweaker tweaker);

    void setLaunchTarget(String target);

    ClassTransformation runTransformation(ClassTransformation transformation);

    String transformClassName(String className);

    String untransformClassName(String className);

    boolean isClassLoadingExclusion(String className);

    boolean isTransformationExclusion(String className);

    Set<LauncherOption> getLauncherOptions();

    boolean isOptionSet(LauncherOption option);
}
