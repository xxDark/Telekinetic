package me.xdark.launcher;

import joptsimple.OptionParser;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.lang.reflect.Constructor;
import java.net.URLClassLoader;
import java.util.Objects;

@Log4j2
public class Main {

    public static void main(String[] args) {
        log.info("Bootstrapping launcher...");
        val parser = new OptionParser();
        val bootstrapperOption = parser.accepts("bootstrapper")
                .withRequiredArg()
                .describedAs("Launcher's bootstrapper class")
                .required();
        val tweakersOptions = parser.accepts("tweakClasses")
                .withRequiredArg()
                .describedAs("Launcher's tweak class(es)")
                .required();
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
            // TODO: call all necessary methods
            log.debug("gotoPhase(INITIALIZATION)");
            launcher.gotoPhase(LaunchPhase.INITIALIZATION);
            val target = launcher.getLaunchTarget();
            Objects.requireNonNull(target, "launchTarget");
            val launchClass = classLoader.loadClass(target);
            val method = launchClass.getMethod("main", String[].class);
            log.debug("gotoPhase(START)");
            launcher.gotoPhase(LaunchPhase.START);
            method.invoke(null, new Object[]{args});
        } catch (Exception ex) {
            log.error("Unable to launch", ex);
            System.exit(1);
        }
    }
}
