package ru.xdark.modloader.di;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.xdark.modloader.util.JavaUtil;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.time.Instant;

public abstract class DefaultAbstractDependenciesInjector extends AbstractDependenciesInjector {

    protected DefaultAbstractDependenciesInjector() {
        super();
        registerInjector(Instant.class, __ -> Instant.now());
        registerInjector(Logger.class, LogManager::getLogger);
        registerInjector(MethodHandles.Lookup.class, __ -> JavaUtil.lookup());
        registerInjector(Unsafe.class, __ -> JavaUtil.unsafe());
    }
}
