package ru.xdark.launcher;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Log4j2
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ApplicationLauncher implements Launcher {
    private final Set<String> classLoadingExclusions = new HashSet<>();
    private final Set<String> transformationExclusions = new HashSet<>();
    private final List<Tweaker> tweakers = new ArrayList<>(8);
    private final List<ClassFileTransformer> transformers = new ArrayList<>(8);
    private final List<ClassFileTransformer> transformersView = Collections.unmodifiableList(transformers);
    private final List<ClassNameTransformer> nameTransformers = new ArrayList<>(4);
    @Getter
    private final Properties properties;
    @Delegate(types = ClasspathAppender.class)
    private LauncherClassLoader classLoader;
    @Getter
    private String launchTarget;

    protected ApplicationLauncher() {
        this(new Properties());
    }

    @Override
    public void inject(LauncherClassLoader classLoader) { // TODO remove this method
        this.classLoader = classLoader;
    }

    @Override
    public Class<?> findLoadedClass(String name) {
        return classLoader.findLoadedClass0(normalizeClassName(name));
    }

    @Override
    public Class<?> findClass(String name, boolean resolve) throws ClassNotFoundException {
        return classLoader.findClass(normalizeClassName(name), resolve);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return classLoader.findClass(normalizeClassName(name));
    }

    @Override
    public boolean isClassLoaded(String name) {
        return classLoader.isClassLoaded(name);
    }

    @Override
    public void addClassLoadingExclusions(String... exclusions) {
        Collections.addAll(this.classLoadingExclusions, exclusions);
        log.info("Added classloading exclusions: {}", Arrays.toString(exclusions));
    }

    @Override
    public void addClassLoadingExclusion(String exclusion) {
        this.classLoadingExclusions.add(exclusion);
        log.info("Added classloading exclusion: {}", exclusion);
    }

    @Override
    public void addTransformerExclusions(String... exclusions) {
        Collections.addAll(this.transformationExclusions, exclusions);
        log.info("Added transformation exclusions: {}", Arrays.toString(exclusions));
    }

    @Override
    public void addTransformerExclusion(String exclusion) {
        this.transformationExclusions.add(exclusion);
        log.info("Added transformation exclusion: {}", exclusion);
    }

    @Override
    public void registerTransformer(ClassFileTransformer transformer) {
        Objects.requireNonNull(transformer, "transformer");
        this.transformers.add(transformer);
        log.info("Registered transformer: {}", transformer);
        if (transformer instanceof ClassNameTransformer) {
            this.nameTransformers.add((ClassNameTransformer) transformer);
            log.info("Registered class name transformer: {}", transformer);
        }
    }

    @Override
    public void registerTweaker(Tweaker tweaker) {
        Objects.requireNonNull(tweaker, "tweaker");
        val tweakers = this.tweakers;
        tweakers.add(tweaker);
        tweakers.sort(Tweaker::compareTo);
        log.info("Registered tweaker: {} w/ tweakOrder: {}", tweaker, tweaker.getTweakOrder());
    }

    @Override
    public void injectTweakers(LauncherInitializationContext context) {
        Objects.requireNonNull(context, "context");
        this.tweakers.forEach(tweaker -> {
            log.info("Calling tweaker: {}", tweaker);
            tweaker.inject(context);
        });
    }

    @Override
    public void setLaunchTarget(String target) {
        log.info("Trying to set launchTarget to: {}", target);
        if (this.launchTarget != null && !isOptionSet(LauncherOption.LAUNCH_TARGET_REDEFINITION)) {
            // Once launchTarget is set, it cannot be modified
            log.warn("Attempted to change launchTarget after it has been set! (from {} to {})", this.launchTarget, target);
            Thread.dumpStack();
            return;
        }
        log.info("Set launcherTarget to: {}", target);
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

    @Override
    public List<ClassFileTransformer> getTransformers() {
        return this.transformersView;
    }

    @Override
    public URL[] getClassPath() {
        return this.classLoader.getURLs();
    }

    @Override
    public URL findResource(String path) {
        return this.classLoader.getResource(path);
    }

    @Override
    public Enumeration<URL> findResources(String path) throws IOException {
        return this.classLoader.getResources(path);
    }

    @Override
    public URL findClassResource(String className) {
        return findResource(className.replace('.', '/') + ".class");
    }

    @Override
    public Enumeration<URL> findClassResources(String className) throws IOException {
        return findResources(className.replace('.', '/') + ".class");
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return this.classLoader.getResourceAsStream(path);
    }

    @Override
    public void setProperty(Object key, Object value) {
        properties.put(key, value);
    }

    @Override
    public <T> T getProperty(Object key) {
        return (T) properties.get(key);
    }

    @Override
    public <T> T getProperty(Object key, T defaultValue) {
        return (T) properties.getOrDefault(key, defaultValue);
    }

    @Override
    public <T> T getProperty(Object key, Supplier<T> defaultValueSupplier) {
        val value = (T) properties.get(key);
        return value != null ? value : defaultValueSupplier.get();
    }

    @Override
    @SneakyThrows
    public void appendUrlToNativePath(URL url) {
        NativeUtil.addPathToNatives(Paths.get(url.toURI()));
        NativeUtil.resetNativeCaches();
    }

    @Override
    public void appendUriToNativePath(URI uri) {
        NativeUtil.addPathToNatives(Paths.get(uri));
        NativeUtil.resetNativeCaches();
    }

    @Override
    @SneakyThrows
    public void appendDirectoryToNativePath(Path directory) {
        NativeUtil.addPathToNatives(directory);
        NativeUtil.resetNativeCaches();
    }

    @Override
    public boolean isOptionSet(LauncherOption option) {
        val options = getLauncherOptions();
        return options != null && options.contains(option);
    }

    @Override
    public void gotoPhase(LaunchPhase phase) {
        this.tweakers.forEach(tweaker -> tweaker.gotoPhase(phase));
    }


    private String tryTransformClassName(String className, BiFunction<ClassNameTransformer, String, String> handler) {
        val nameTransformers = this.nameTransformers;
        for (int i = 0, j = nameTransformers.size(); i < j; i++) {
            val transformer = nameTransformers.get(i);
            val newName = handler.apply(transformer, className);
            if (!isOptionSet(LauncherOption.MULTIPLE_NAME_TRANSFORMERS)) {
                return newName == null ? className : newName;
            }
            if (newName != null) return newName;
        }
        return className;
    }

    private static String normalizeClassName(String name) {
        return name.replace('/', '.');
    }
}
