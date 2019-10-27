package ru.xdark.modloader.di;

import lombok.val;

import java.lang.reflect.Modifier;

public final class ReflectiveDependenciesInjector extends DefaultAbstractDependenciesInjector {
    @Override
    public int inject(Object o) {
        int count = 0;
        for (val field : o.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class)) continue;
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
                field.set(o, injector.inject(o));
            } catch (Exception ex) {
                throw new RuntimeException("Dependency injection has failed", ex);
            }
            count += 1;
        }
        return count;
    }
}
