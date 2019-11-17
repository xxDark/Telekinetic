package ru.xdark.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

public interface ClassLoadingController extends ClasspathAppender {

    Class<?> findLoadedClass(String name);

    Class<?> findClass(String name, boolean resolve) throws ClassNotFoundException;

    Class<?> findClass(String name) throws ClassNotFoundException;

    boolean isClassLoaded(String name);

    void addClassLoadingExclusions(String... exclusions);

    void addClassLoadingExclusion(String exclusion);

    void addTransformerExclusions(String... exclusions);

    void addTransformerExclusion(String exclusion);

    void registerTransformer(ClassFileTransformer transformer);

    ClassTransformation runTransformation(ClassTransformation transformation);

    String transformClassName(String className);

    String untransformClassName(String className);

    boolean isClassLoadingExclusion(String className);

    boolean isTransformationExclusion(String className);

    List<ClassFileTransformer> getTransformers();

    URL[] getClassPath();

    URL findResource(String path);

    Enumeration<URL> findResources(String path) throws IOException;

    URL findClassResource(String className);

    Enumeration<URL> findClassResources(String className) throws IOException;

    InputStream getResourceAsStream(String path);
}
