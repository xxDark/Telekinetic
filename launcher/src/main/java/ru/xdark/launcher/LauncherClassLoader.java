package ru.xdark.launcher;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.function.Predicate;

@Log4j2
public final class LauncherClassLoader extends URLClassLoader implements ClasspathAppender {
    private static final ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[1024]);
    private final ClassLoadingController controller;
    private final ClassLoader parent;

    LauncherClassLoader(ClassLoadingController controller, URL[] urls, ClassLoader parent) {
        super(urls, null);
        this.parent = parent;
        this.controller = controller;
        controller.addClassLoadingExclusions(
                "java.",
                "sun.",
                "ru.xdark.launcher.",
                "ru.xdark.telekinetic."
        );
        controller.addTransformerExclusions(
                "javax.",
                "org.objectweb.",
                "com.google."
        );
        if (parent instanceof URLClassLoader) {
            for (val url : ((URLClassLoader) parent).getURLs()) {
                addURL(url);
            }
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, false);
    }

    Class<?> findLoadedClass0(String name) {
        return findLoadedClass(name);
    }

    Class<?> findClass(String name, boolean resolve) throws ClassNotFoundException {
        val handle = this.controller;
        if (handle.isClassLoadingExclusion(name)) {
            return parent.loadClass(name);
        }
        {
            val cached = findLoadedClass(name);
            if (cached != null) return cached;
        }
        if (handle.isTransformationExclusion(name)) {
            return super.findClass(name);
        }
        try {
            val transformed = handle.transformClassName(name);
            val cached = findLoadedClass(transformed);
            if (cached != null) return cached;
            val untransformed = handle.untransformClassName(name);
            val resource = findResource(untransformed.replace('.', '/') + ".class");
            if (resource == null) {
                return parent.loadClass(name);
            }
            val lastDot = untransformed.lastIndexOf('.');
            val packageName = lastDot == -1 ? "" : untransformed.substring(0, lastDot);
            URL location = resource;
            val connection = resource.openConnection();
            connection.setDoInput(true);
            CodeSigner[] signers = null;
            if (connection instanceof JarURLConnection) {
                val jarConnection = (JarURLConnection) connection;
                location = jarConnection.getJarFileURL();
                val manifest = jarConnection.getManifest();
                if (manifest != null) {
                    val entry = jarConnection.getJarEntry();
                    signers = entry.getCodeSigners();
                    if (getPackage(packageName) == null) {
                        definePackage(packageName, manifest, jarConnection.getJarFileURL());
                    }
                }
            } else {
                if (getPackage(packageName) == null) {
                    definePackage(packageName, null, null, null, null, null, null, null);
                }
            }
            val buffer = BUFFER.get();
            byte[] classBytes;
            try (val in = connection.getInputStream()) {
                val baos = new ByteArrayOutputStream(256);
                for (int r = in.read(buffer); r != -1; r = in.read(buffer)) {
                    baos.write(buffer, 0, r);
                }
                classBytes = baos.toByteArray();
            }
            val result = handle.runTransformation(new ClassTransformation(name, transformed, untransformed, classBytes));
            val toDefine = result.getClassBytes();
            val codeSource = new CodeSource(location, signers);
            val klass = defineClass(transformed, toDefine, 0, toDefine.length, codeSource);
            if (resolve) resolveClass(klass);
            return klass;
        } catch (Exception ex) {
            throw new ClassNotFoundException(name, ex);
        }
    }

    boolean isClassLoaded(String name) {
        return findLoadedClass(name) != null;
    }

    @Override
    public void appendUrlToClassPath(URL url) {
        addURL(url);
    }

    @Override
    @SneakyThrows
    public void appendUriToClassPath(URI uri) {
        addURL(uri.toURL());
    }

    @Override
    @SneakyThrows
    public void appendDirectoryToClassPath(Path directory, Predicate<Path> filter) {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                val result = super.visitFile(file, attrs);
                if (filter == null || filter.test(file)) {
                    appendUriToClassPath(file.toUri());
                }
                return result;
            }
        });
    }

    static {
        ClassLoader.registerAsParallelCapable();
    }
}
