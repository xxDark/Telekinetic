package ru.xdark.telekinetic.di;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public final class ReflectiveDependenciesInjector extends DefaultAbstractDependenciesInjector {

    public ReflectiveDependenciesInjector(Map<Class<?>, Injector<?>> injectorsMap) {
        super(injectorsMap);
    }

    public ReflectiveDependenciesInjector(DependenciesInjector injector) {
        super(injector);
    }

    public ReflectiveDependenciesInjector() {
        super();
    }

    @Override
    public int inject(Object o) {
        List<ObjectInjector> injectors = new ArrayList<>(4);
        val clazz = o.getClass();
        for (val field : clazz.getDeclaredFields()) {
            val annotation = field.getDeclaredAnnotation(Inject.class);
            if (annotation == null) continue;
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
            injectors.add(new FieldInjector(annotation.order(), o, injector, field));
        }
        for (val method : clazz.getDeclaredMethods()) {
            val annotation = method.getDeclaredAnnotation(Inject.class);
            if (annotation == null) continue;
            log.debug("Found injectable method: {}", method);
            method.setAccessible(true);
            injectors.add(new MethodInjector(annotation.order(), o, method));
        }
        injectors.sort(ObjectInjector::compareTo);
        for (val injector : injectors) {
            try {
                injector.inject();
            } catch (Exception ex) {
                throw new RuntimeException("Dependency injection has failed", ex);
            }
        }
        return injectors.size();
    }

    @Override
    public DependenciesInjector toImmutable() {
        return new ImmutableDependenciesInjector(this);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static abstract class ObjectInjector implements Comparable<ObjectInjector> {

        private final int order;

        abstract void inject() throws Exception;

        @Override
        public int compareTo(ObjectInjector o) {
            return o.order - order;
        }
    }

    private static class FieldInjector extends ObjectInjector {

        private final Object o;
        private final ru.xdark.telekinetic.di.Injector<?> injector;
        private final Field field;

        private FieldInjector(int order, Object o, ru.xdark.telekinetic.di.Injector<?> injector, Field field) {
            super(order);
            this.o = o;
            this.injector = injector;
            this.field = field;
        }

        @Override
        public void inject() throws Exception {
            val o = this.o;
            val value = injector.inject(o);
            field.set(o, value);
        }
    }

    private static class MethodInjector extends ObjectInjector {

        private final Object o;
        private final Method method;

        private MethodInjector(int order, Object o, Method method) {
            super(order);
            this.o = o;
            this.method = method;
        }

        @Override
        public void inject() throws Exception {
            method.invoke(o);
        }
    }
}
