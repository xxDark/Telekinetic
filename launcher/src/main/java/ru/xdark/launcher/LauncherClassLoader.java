package ru.xdark.launcher;

import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;

@Log4j2
public final class LauncherClassLoader extends URLClassLoader {
    private static final ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[1024]);
    private final Launcher launcher;
    private final ClassLoader parent;

    LauncherClassLoader(Launcher launcher, URL[] urls, ClassLoader parent) {
        super(urls, null);
        this.parent = parent;
        this.launcher = launcher;
        initialize(launcher);
    }

    private static void initialize(Launcher launcher) {
        log.debug("Initializing launcher {}", launcher);
        launcher.addClassLoadingExclusions(
                "java.",
                "sun.",
                "com.sun.",
                "me.xdark.modloader.",
                "org.apache.logging."
        );
        launcher.addTransformerExclusions(
                "javax.",
                "org.objectweb.",
                "com.google."
        );
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        val handle = this.launcher;
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
            val resource = getResource(untransformed.replace('.', '/') + ".class");
            if (resource == null) {
                throw new ClassNotFoundException(untransformed);
            }
            val lastDot = untransformed.lastIndexOf('.');
            val packageName = lastDot == -1 ? "" : untransformed.substring(0, lastDot);
            val connection = resource.openConnection();
            connection.setDoInput(true);
            CodeSigner[] signers = null;
            if (connection instanceof JarURLConnection) {
                val jarConnection = (JarURLConnection) connection;
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
            val codeSource = new CodeSource(resource, signers);
            return defineClass(transformed, classBytes, 0, classBytes.length, codeSource);
        } catch (Exception ex) {
            throw new ClassNotFoundException(name, ex);
        }
    }

    static {
        ClassLoader.registerAsParallelCapable();
    }
}
