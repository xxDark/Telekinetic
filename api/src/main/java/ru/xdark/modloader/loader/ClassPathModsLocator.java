package ru.xdark.modloader.loader;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import ru.xdark.launcher.Launcher;
import ru.xdark.launcher.LauncherClassLoader;
import ru.xdark.modloader.mod.Mod;
import ru.xdark.modloader.mod.ModContainer;
import ru.xdark.modloader.util.JavaUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.jar.JarFile;

@Log4j2
public final class ClassPathModsLocator implements ModsLocator {

    private static final String MANIFEST_KEY = "Mod-Class";

    @Override
    public Collection<ModContainer> findContainers(Launcher launcher, LauncherClassLoader classLoader) {
        val classPath = System.getProperty("java.class.path").split(JavaUtil.cpSeparator());
        log.debug("Java ClassPath ({})", Arrays.toString(classPath));
        val toLoad = new HashMap<Class<?>, Mod>();
        for (val entry : classPath) {
            log.debug("Scanning classpath entry: {}", entry);
            try (val jar = new JarFile(entry, true)) {
                val manifest = jar.getManifest();
                if (manifest == null) {
                    log.debug("Skipping {}: no manifest", entry);
                    continue;
                }
                val attributes = manifest.getMainAttributes();
                if (attributes == null) {
                    log.debug("Skipping {}: no main attributes", entry);
                    continue;
                }
                val className = attributes.getValue(MANIFEST_KEY);
                if (className == null) {
                    log.debug("Skipping {}: no '{}' entry", entry, MANIFEST_KEY);
                    continue;
                }
                log.info("Discovered mod: {}", className);
                // Load class (this will NOT trigger static initializer)
                Class<?> modClass;
                try {
                    modClass = Class.forName(className.replace('/', '.'), false, classLoader);
                } catch (ClassNotFoundException ex) {
                    throw new IllegalStateException("Mod class is missing: " + className);
                }
                val modAnnotation = modClass.getAnnotation(Mod.class);
                if (modAnnotation == null) {
                    log.error("Mod {} is missing @Mod annotation!", modClass);
                    continue;
                }
                toLoad.put(modClass, modAnnotation);
            } catch (IOException ex) {
                log.error("Error scanning classpath entry:", ex);
            }
        }
        // TODO: implement mods loading
        throw new UnsupportedOperationException();
    }
}
