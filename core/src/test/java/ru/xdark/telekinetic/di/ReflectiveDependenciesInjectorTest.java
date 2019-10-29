package ru.xdark.telekinetic.di;

import lombok.val;
import org.junit.jupiter.api.Test;
import ru.xdark.telekinetic.util.JavaUtil;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectiveDependenciesInjectorTest {

    @Test
    public void testInjection() {
        val injector = new ReflectiveDependenciesInjector();
        val testClass = new TestClass();
        assertEquals(2, injector.inject(testClass));
        assertNotNull(testClass.instant);
        assertEquals(JavaUtil.unsafe(), testClass.unsafe);
        assertNull(testClass.lookup);
    }

    @Test
    public void testFail() {
        val injector = new ReflectiveDependenciesInjector();
        assertThrows(IllegalStateException.class, () -> injector.inject(new FailureTestClass()));
    }

    private static class TestClass {
        @Inject
        Instant instant;
        @Inject
        Unsafe unsafe;
        MethodHandles.Lookup lookup;
    }

    private static class FailureTestClass {
        @Inject
        final Instant instant = Instant.now();
    }
}
