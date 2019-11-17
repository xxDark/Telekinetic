package ru.xdark.launcher;

import joptsimple.OptionParser;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.jar.JarFile;

@Log4j2
public class Main {

    public static void main(String[] args) {
        log.info("Bootstrapping launcher...");
        val parser = new OptionParser();
        val bootstrapperOption = parser.accepts("bootstrapper", "Launcher's bootstrapper class")
                .withRequiredArg()
                .required();
        val tweakersOptions = parser.accepts("tweakClasses", "Launcher's tweak class(es)")
                .withRequiredArg();
        val gameDirectoryOption = parser.accepts("workingDir", "Game's launch directory")
                .withRequiredArg()
                .withValuesConvertedBy(new PathValueConverter())
                .defaultsTo(Paths.get("."));
        val librariesOption = parser.accepts("libsDir", "Libraries directory to load")
                .withRequiredArg()
                .withValuesConvertedBy(new PathValueConverter());
        val nativesOption = parser.accepts("nativesDir", "Native libraries directory to load")
                .withRequiredArg()
                .withValuesConvertedBy(new PathValueConverter());
        val helpOption = parser.acceptsAll(Arrays.asList("h", "help"), "Prints help").forHelp();
        parser.allowsUnrecognizedOptions();

        val options = parser.parse(args);
        if (options.has(helpOption)) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException ex) {
                log.error(ex);
            }
            System.exit(0);
        }

        val bootstrapperClass = options.valueOf(bootstrapperOption);
        log.info("Bootstrap class: {}", bootstrapperClass);
        val ourLoader = Main.class.getClassLoader();
        Launcher launcher;
        try {
            val constructor = (Constructor<LauncherBootstrapper>) ourLoader.loadClass(bootstrapperClass).getDeclaredConstructor();
            constructor.setAccessible(true);
            launcher = constructor.newInstance().create();
        } catch (Exception ex) {
            log.error("Unable to launch", ex);
            System.exit(1);
            return;
        }
        RootLauncher.set(launcher);
        try {
            val classLoader = new LauncherClassLoader(launcher, ((URLClassLoader) ourLoader).getURLs(), ourLoader);
            launcher.inject(classLoader);
            log.info("gotoPhase(PRE_INITIALIZATION)");
            launcher.gotoPhase(LaunchPhase.makePhase(null, LaunchPhase.PhaseType.PRE_INITIALIZATION));

            // TODO remove me?
            val libsDir = options.valuesOf(librariesOption);
            log.info("Libraries directories: {}", libsDir);
            if (libsDir != null) {
                for (val dir : libsDir) {
                    if (!Files.isDirectory(dir)) {
                        throw new NoSuchFileException(dir.normalize().toString());
                    }
                    launcher.appendDirectoryToClassPath(dir, path -> path.getFileName().toString().endsWith(".jar"));
                }
            }
            val nativesDir = options.valuesOf(nativesOption);
            if (nativesDir != null) {
                for (val dir : nativesDir) {
                    if (!Files.isDirectory(dir)) {
                        throw new NoSuchFileException(dir.normalize().toString());
                    }
                    launcher.appendDirectoryToNativePath(dir);
                }
            }
            //

            val tweakers = options.valuesOf(tweakersOptions);
            log.info("Injecting tweakers: {}", tweakers);
            for (val className : tweakers) {
                val constructor = (Constructor<Tweaker>) classLoader.loadClass(className).getDeclaredConstructor();
                constructor.setAccessible(true);
                val tweaker = constructor.newInstance();
                launcher.registerTweaker(tweaker);
            }
            log.info("Scanning for classpath tweakers");
            for (val url : classLoader.getURLs()) {
                try {
                    val uri = url.toURI();
                    try (val jarFile = new JarFile(new File(uri), true)) {
                        val manifest = jarFile.getManifest();
                        if (manifest == null) continue;
                        val attrs = manifest.getMainAttributes();
                        if (attrs == null) continue;
                        val classNames = attrs.getValue("TTweakClass");
                        if (classNames == null) continue;
                        log.info("Detected jar file with Telekinetic tweakers: {}/{}", uri, classNames);
                        for (val className : classNames.split(",")) {
                            val constructor = (Constructor<Tweaker>) classLoader.loadClass(className).getDeclaredConstructor();
                            constructor.setAccessible(true);
                            val tweaker = constructor.newInstance();
                            launcher.registerTweaker(tweaker);
                        }
                    }
                } catch (Exception ex) {
                    log.warn("Unable to open {}", url, ex);
                }
            }
            log.info("gotoPhase(INITIALIZATION)");
            val workingDir = options.valueOf(gameDirectoryOption);
            val arguments = new ArrayList<>(Arrays.asList(args));
            val context = new LauncherInitializationContext(
                    launcher,
                    classLoader,
                    arguments,
                    workingDir
            );
            launcher.gotoPhase(LaunchPhase.makePhase(context, LaunchPhase.PhaseType.INITIALIZATION));
            launcher.injectTweakers(context);
            log.info("gotoPhase(ABOUT_TO_START)");
            launcher.gotoPhase(LaunchPhase.makePhase(context, LaunchPhase.PhaseType.ABOUT_TO_START));
            val target = launcher.getLaunchTarget();
            Objects.requireNonNull(target, "launchTarget");
            val launchClass = classLoader.loadClass(target);
            val main = MethodHandles.publicLookup().findStatic(
                    launchClass,
                    "main",
                    MethodType.methodType(Void.TYPE, String[].class)
            ).asFixedArity();
            log.info("gotoPhase(START)");
            launcher.gotoPhase(LaunchPhase.makePhase(context, LaunchPhase.PhaseType.START));
            main.invokeExact((String[]) arguments.toArray(new String[0]));
        } catch (Throwable ex) {
            log.error("Unable to launch", ex);
            System.exit(1);
        }
    }
}
