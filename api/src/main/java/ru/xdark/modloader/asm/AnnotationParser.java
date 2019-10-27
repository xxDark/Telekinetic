package ru.xdark.modloader.asm;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class AnnotationParser {

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass, ClassReader reader, ClassLoader loader) {
        val targetDesc = Type.getDescriptor(annotationClass);
        Map<String, Object>[] ref = new Map[1];
        reader.accept(new ClassVisitor(Opcodes.ASM7) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if (!targetDesc.equals(descriptor)) return null;
                val map = ref[0] = new HashMap<>(4);
                return new AnnotationVisitor(Opcodes.ASM7, super.visitAnnotation(descriptor, visible)) {

                    @Override
                    public void visit(String name, Object value) {
                        map.put(name, value);
                    }

                    @Override
                    public void visitEnum(String name, String descriptor, String value) {
                        val type = Type.getType(descriptor);
                        val className = type.getClassName();
                        Class<? extends Enum> enumClass;
                        try {
                            enumClass = (Class<? extends Enum>) loader.loadClass(className);
                        } catch (ClassNotFoundException ex) {
                            throw new IllegalStateException("Cannot parse annotation: missing required class name:" + className);
                        }
                        map.put(name, Enum.valueOf(enumClass, value));
                    }
                };
            }
        }, ClassReader.SKIP_CODE);
        val map = ref[0];
        if (map == null) return null;
        return (T) sun.reflect.annotation.AnnotationParser.annotationForMap(annotationClass, map);
    }
}
