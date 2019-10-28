package ru.xdark.telekenetic.di;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.xdark.telekenetic.util.JavaUtil;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.Map;

public abstract class DefaultAbstractDependenciesInjector extends AbstractDependenciesInjector {

    protected DefaultAbstractDependenciesInjector(Map<Class<?>, Injector<?>> injectorsMap) {
        super(injectorsMap);
        registerDefaultInjectors();
    }

    protected DefaultAbstractDependenciesInjector(DependenciesInjector injector) {
        super(injector);
        registerDefaultInjectors();
    }

    protected DefaultAbstractDependenciesInjector() {
        super();
        registerDefaultInjectors();
    }

    private void registerDefaultInjectors() {
        registerInjector(Instant.class, __ -> Instant.now());
        registerInjector(Logger.class, LogManager::getLogger);
        registerInjector(MethodHandles.Lookup.class, __ -> JavaUtil.lookup());
        registerInjector(Unsafe.class, __ -> JavaUtil.unsafe());
    }
}
