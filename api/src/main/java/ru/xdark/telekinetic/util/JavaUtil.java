package ru.xdark.telekinetic.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

@UtilityClass
public class JavaUtil {

    private final Unsafe UNSAFE;
    private final MethodHandles.Lookup LOOKUP;
    private final int VM_VERSION;

    public Unsafe unsafe() {
        return UNSAFE;
    }

    public MethodHandles.Lookup lookup() {
        return LOOKUP;
    }

    public int vmVersion() {
        return VM_VERSION;
    }

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            val unsafe = UNSAFE = (Unsafe) field.get(null);
            field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            val lookup = LOOKUP = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
            val vmVersion = Float.parseFloat(System.getProperty("java.class.version"));
            try (val in = ClassLoader.getSystemResourceAsStream("java/lang/Object.class")) {
                in.skip(6);
                val classVersion = (in.read() << 8) + in.read();
                if (vmVersion != classVersion) {
                    throw new RuntimeException("'java.class.version' and java/lang/Object class version mismatch! (expected: " + vmVersion + ", but got: " + classVersion + ")");
                }
                VM_VERSION = (int) (vmVersion - 44);
            }
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }
}
