package ru.xdark.telekinetic.mod;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import ru.xdark.telekinetic.version.Version;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

@Log4j2
public final class ClassPathModsLocator implements ModsLocator {
    private static final String FMLAT_MANIFEST_KEY = "FMLAT";
    private static final String MOD_MANIFEST_KEY = "Mod-Class";

    @Override
    public Collection<ModContainer> findContainers(ModsLocateContext ctx) {
        val classLoader = ctx.getClassLoader();
        val classPath = classLoader.getURLs();
        log.debug("Java ClassPath ({})", Arrays.toString(classPath));
        val toLoad = new HashMap<String, ModInformation>();
        val launcher = ctx.getLauncher();
        for (val entry : classPath) {
            log.debug("Scanning classpath entry: {}", entry);
            try (val jar = new JarFile(new File(entry.toURI()), true)) {
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
                // Detect FMLAT
                val fmlat = attributes.getValue(FMLAT_MANIFEST_KEY);
                if (fmlat != null) {
                    log.debug("Detected FMLAT: {}", entry);
                    launcher.registerTransformer(new AccessTransformer(jar, fmlat));
                }
                val className = attributes.getValue(MOD_MANIFEST_KEY);
                if (className == null) {
                    log.debug("Skipping {}: no '{}' entry", entry, MOD_MANIFEST_KEY);
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
                toLoad.put(modAnnotation.id(), new ModInformation(modClass, modAnnotation));
            } catch (IOException | URISyntaxException ex) {
                log.error("Error scanning classpath entry:", ex);
            }
        }
        int size = toLoad.size();
        log.info("Discovered {} mods", size);
        if (size != 0) {
            Iterator<Map.Entry<String, ModInformation>> iterator = toLoad.entrySet().iterator();
            while (size-- > 0) {
                val entry = iterator.next();
                val information = entry.getValue();
                checkDependencies(toLoad, information.modAnnotation);
            }
            // Everything is verified, construct mods
            size = toLoad.size();
            iterator = new HashMap<>(toLoad).entrySet().iterator();
            val mods = new ArrayList<ModContainer>(size);
            while (size-- > 0) {
                try {
                    loadMod(mods, toLoad, iterator.next().getValue());
                } catch (IllegalAccessException | InstantiationException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return mods;
        }
        return Collections.emptyList();
    }

    private void loadMod(List<ModContainer> containers, Map<String, ModInformation> mods, ModInformation loading) throws IllegalAccessException, InstantiationException {
        val annotation = loading.modAnnotation;
        val dependencies = annotation.dependencies();
        for (int i = 0, j = dependencies.length; i < j; i++) {
            val dependency = dependencies[i];
            val id = dependency.id();
            val mod = mods.get(id);
            if (mod == null) continue;
            loadMod(containers, mods, mod);
        }
        mods.remove(annotation.id());
        containers.add(new LoadedModContainer(
                ModDescription.fromAnnotation(annotation),
                loading.modClass.newInstance()
        ));
    }

    private void checkDependencies(Map<String, ModInformation> mods, Mod modAnnotation) {
        val dependencies = modAnnotation.dependencies();
        int length = dependencies.length;
        while (length-- > 0) {
            val dependency = dependencies[length];
            val id = dependency.id();
            val optional = dependency.optional();
            val information = mods.get(id);
            if (information == null) {
                if (optional) {
                    log.debug("Skipping {}: dependency is optional", id);
                    continue;
                }
                throw new IllegalStateException("Missing required dependency! (" + id + ')');
            }
            val dependencyVersion = dependency.version();
            checkVersion(information.modAnnotation.version(), dependencyVersion);
        }
    }

    private void checkVersion(String current, String required) {
        val cVersion = Version.parse(current);
        val rVersion = Version.parse(required);
        if (cVersion.isLessThan(rVersion)) {
            throw new IllegalStateException("Current version is less than required! (" + cVersion + " < " + rVersion + ')');
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ModInformation {

        final Class<?> modClass;
        final Mod modAnnotation;
    }
}
