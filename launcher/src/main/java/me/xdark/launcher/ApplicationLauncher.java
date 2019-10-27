package me.xdark.launcher;

import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

@Log4j2
public abstract class ApplicationLauncher implements Launcher {
    private final Set<String> classLoadingExclusions = new HashSet<>();
    private final Set<String> transformationExclusions = new HashSet<>();
    private final List<Tweaker> tweakers = new ArrayList<>(8);
    private final List<ClassFileTransformer> transformers = new ArrayList<>(8);
    private final List<ClassNameTransformer> nameTransformers = new ArrayList<>(4);
    private String launchTarget;

    @Override
    public void addClassLoadingExclusions(String... exclusions) {
        Collections.addAll(this.classLoadingExclusions, exclusions);
        log.debug("Added classloading exclusions: {}", Arrays.toString(exclusions));
    }

    @Override
    public void addTransformerExclusions(String... exclusions) {
        Collections.addAll(this.transformationExclusions, exclusions);
        log.debug("Added transformation exclusions: {}", Arrays.toString(exclusions));
    }

    @Override
    public void registerTransformer(ClassFileTransformer transformer) {
        Objects.requireNonNull(transformer, "transformer");
        this.transformers.add(transformer);
        log.debug("Registered transformer: {}", transformer);
        if (transformer instanceof ClassNameTransformer) {
            this.nameTransformers.add((ClassNameTransformer) transformer);
            log.debug("Registered class name transformer: {}", transformer);
        }
    }

    @Override
    public void registerTweaker(Tweaker tweaker) {
        Objects.requireNonNull(tweaker, "tweaker");
        val tweakers = this.tweakers;
        tweakers.add(tweaker);
        tweakers.sort(Tweaker::compareTo);
        log.debug("Registered tweaker: {} w/ tweakOrder: {}", tweaker, tweaker.getTweakOrder());
    }

    @Override
    public void setLaunchTarget(String target) {
        log.debug("Trying to set launchTarget to: {}", target);
        if (this.launchTarget != null && !isOptionSet(LauncherOption.LAUNCH_TARGET_REDEFINITION)) {
            // Once launchTarget is set, it cannot be modified
            log.warn("Attempted to change launchTarget after it has been set! (from {} to {})", this.launchTarget, target);
            Thread.dumpStack();
            return;
        }
        log.debug("Set launcherTarget to: {}", target);
        this.launchTarget = target;
    }

    @Override
    public ClassTransformation runTransformation(ClassTransformation transformation) {
        val transformers = this.transformers;
        for (int i = 0, j = transformers.size(); i < j; i++) {
            val transformer = transformers.get(i);
            try {
                transformer.transform(transformation);
            } catch (Exception ex) {
                log.error("Transformer {} has failed to process transformation; we will continue transformation, but notify the author of the mod!", transformer, ex);
            }
        }
        return transformation;
    }

    @Override
    public String transformClassName(String className) {
        return tryTransformClassName(className, ClassNameTransformer::transform);
    }

    @Override
    public String untransformClassName(String className) {
        return tryTransformClassName(className, ClassNameTransformer::untransform);
    }

    @Override
    public boolean isClassLoadingExclusion(String className) {
        return this.classLoadingExclusions.stream().anyMatch(className::startsWith);
    }

    @Override
    public boolean isTransformationExclusion(String className) {
        return this.transformationExclusions.stream().anyMatch(className::startsWith);
    }

    private String tryTransformClassName(String className, BiFunction<ClassNameTransformer, String, String> handler) {
        val nameTransformers = this.nameTransformers;
        for (int i = 0, j = nameTransformers.size(); i < j; i++) {
            val transformer = nameTransformers.get(i);
            val newName = handler.apply(transformer, className);
            if (!isOptionSet(LauncherOption.MULTIPLE_NAE_TRANSFORMERS)) {
                return newName == null ? className : newName;
            }
            if (newName != null) return newName;
        }
        return className;
    }

    @Override
    public boolean isOptionSet(LauncherOption option) {
        val options = getLauncherOptions();
        return options != null && options.contains(option);
    }
}
