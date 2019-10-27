package me.xdark.launcher;

public interface Launcher {

    void addClassLoadingExclusions(String... exclusions);

    void addTransformerExclusions(String... exclusions);

    void registerTransformer(ClassFileTransformer transformer);

    void registerTweaker(Tweaker tweaker);

    void setLaunchTarget(String target);

    ClassTransformation runTransformation(ClassTransformation transformation);

    boolean isClassLoadingExclusion(String className);

    boolean isTransformationExclusion(String className);
}
