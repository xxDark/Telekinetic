package ru.xdark.launcher;

import joptsimple.OptionParser;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
        val gameDirectoryOption = parser.accepts("gameDir", "Game's launch directory")
                .withRequiredArg()
                .withValuesConvertedBy(new PathValueConverter());
        val assetsDirectoryOption = parser.accepts("assetsDir", "Game's assets directory")
                .withRequiredArg()
                .withValuesConvertedBy(new PathValueConverter());
        val librariesOption = parser.accepts("libsDir", "Libraries directory to load")
                .withRequiredArg()
                .withValuesConvertedBy(new PathValueConverter());
        val nativesOption = parser.accepts("nativesDir", "Native libraries directory to load")
                .withRequiredArg()
                .withValuesConvertedBy(new PathValueConverter());
        val versionOption = parser.accepts("version", "Game's version").withRequiredArg();

        parser.allowsUnrecognizedOptions();
        val options = parser.parse(args);
        String bootstrapperClass = options.valueOf(bootstrapperOption);
        log.debug("Bootstrap class: {}", bootstrapperClass);
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
        try {
            val classLoader = new LauncherClassLoader(launcher, ((URLClassLoader) ourLoader).getURLs(), ourLoader);
            launcher.inject(classLoader);
            log.debug("gotoPhase(PRE_INITIALIZATION)");
            launcher.gotoPhase(LaunchPhase.PRE_INITIALIZATION);
            val libsDir = options.valueOf(librariesOption);
            log.debug("Libraries directory: {}", libsDir);
            if (libsDir != null) {
                if (!Files.isDirectory(libsDir)) {
                    throw new NoSuchFileException(libsDir.normalize().toString());
                }
                launcher.appendDirectoryToClassPath(libsDir, path -> path.getFileName().toString().endsWith(".jar"));
            }
            val nativesDir = options.valueOf(nativesOption);
            if (nativesDir != null) {
                if (!Files.isDirectory(nativesDir)) {
                    throw new NoSuchFileException(nativesDir.normalize().toString());
                }
                launcher.appendDirectoryToNativePath(nativesDir);
            }
            val tweakers = options.valuesOf(tweakersOptions);
            log.debug("Injecting tweakers: {}", tweakers);
            for (val className : tweakers) {
                launcher.addClassLoadingExclusions(className.substring(0, className.lastIndexOf('.')));
                val constructor = (Constructor<Tweaker>) classLoader.loadClass(className).getDeclaredConstructor();
                constructor.setAccessible(true);
                val tweaker = constructor.newInstance();
                launcher.registerTweaker(tweaker);
            }
            log.debug("gotoPhase(INITIALIZATION)");
            launcher.gotoPhase(LaunchPhase.INITIALIZATION);

            val arguments = new ArrayList<>(Arrays.asList(args));
            val context = new LauncherInitializationContext(
                    launcher,
                    classLoader,
                    arguments,
                    options.valueOf(gameDirectoryOption),
                    options.valueOf(assetsDirectoryOption),
                    options.valueOf(versionOption)
            );
            launcher.injectTweakers(context);
            val target = launcher.getLaunchTarget();
            Objects.requireNonNull(target, "launchTarget");
            val launchClass = classLoader.loadClass(target);
            val main = MethodHandles.publicLookup().findStatic(
                    launchClass,
                    "main",
                    MethodType.methodType(Void.TYPE, String[].class)
            ).asFixedArity();
            log.debug("gotoPhase(START)");
            launcher.gotoPhase(LaunchPhase.START);
            main.invokeExact((String[]) arguments.toArray(new String[0]));
        } catch (Throwable ex) {
            log.error("Unable to launch", ex);
            System.exit(1);
        }
    }
}
