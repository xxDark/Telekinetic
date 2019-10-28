package ru.xdark.launcher;

import com.google.common.collect.Lists;
import joptsimple.OptionParser;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.lang.reflect.Constructor;
import java.net.URLClassLoader;
import java.nio.file.Path;
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
                .withRequiredArg()
                .required();
        val gameDirectoryOption = parser.accepts("gameDir", "Game's launch directory")
                .withRequiredArg()
                .ofType(Path.class)
                .withValuesConvertedBy(new PathValueConverter());
        val assetsDirectoryOption = parser.accepts("assetsDir", "Game's assets directory")
                .withRequiredArg()
                .ofType(Path.class)
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
            log.debug("gotoPhase(PRE_INITIALIZATION)");
            launcher.gotoPhase(LaunchPhase.PRE_INITIALIZATION);
            val classLoader = new LauncherClassLoader(launcher, ((URLClassLoader) ourLoader).getURLs(), ourLoader);
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

            val arguments = Lists.newArrayList(args);
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
            val method = launchClass.getMethod("main", String[].class);
            log.debug("gotoPhase(START)");
            launcher.gotoPhase(LaunchPhase.START);
            method.invoke(null, new Object[]{arguments.toArray(new String[0])});
        } catch (Exception ex) {
            log.error("Unable to launch", ex);
            System.exit(1);
        }
    }
}
