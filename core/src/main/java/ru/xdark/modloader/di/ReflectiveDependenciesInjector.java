package ru.xdark.modloader.di;

import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.lang.reflect.Modifier;

@Log4j2
public final class ReflectiveDependenciesInjector extends DefaultAbstractDependenciesInjector {

    @Override
    public int inject(Object o) {
        int count = 0;
        for (val field : o.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class)) continue;
            log.debug("Found injectable field: {}", field);
            field.setAccessible(true);
            val type = field.getType();
            val injector = getInjector(type);
            if (injector == null) {
                throw new IllegalArgumentException("No such injector: " + type);
            }
            if (Modifier.isFinal(field.getModifiers())) {
                throw new IllegalStateException("Cannot inject into final field: " + field);
            }
            try {
                val value = injector.inject(o);
                field.set(o, value);
                log.debug("Injected {} for {}, value {}", field, o, value);
            } catch (Exception ex) {
                throw new RuntimeException("Dependency injection has failed", ex);
            }
            count += 1;
        }
        return count;
    }

    @Override
    public DependenciesInjector toImmutable() {
        return new ImmutableDependenciesInjector(this);
    }
}
