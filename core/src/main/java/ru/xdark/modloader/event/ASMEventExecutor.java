package ru.xdark.modloader.event;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import ru.xdark.modloader.EventExecutor;
import ru.xdark.modloader.util.JavaUtil;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

@UtilityClass
final class ASMEventExecutor {

    private final String MAGIC_ACCESSOR_CLASS_NAME = "MagicAccessorImpl";
    private final String MAGIC_ACCESSOR_BRIDGE_CLASS_NAME = "MagicAccessorImplBridge";
    private final String BRIDGE_INTERNAL = Type.getInternalName(createMagicBridge());

    @SneakyThrows
    EventExecutor<?> generateExecutor(Object listener, Method method, ClassLoader loader) {
        val writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        val owner = method.getDeclaringClass();
        val ownerInternal = Type.getInternalName(owner);
        val event = method.getParameterTypes()[0];
        val eventInternal = Type.getInternalName(event);
        val methodName = method.getName();
        val executorName = "ru/xdark/modloader/event/EventExecutor$$" + owner.getName().replace('.', '$') + "__" + methodName;
        writer.visit(JavaUtil.vmVersion() + 44, ACC_FINAL, executorName, null, BRIDGE_INTERNAL, new String[]{"ru/xdark/modloader/event/EventExecutor"});
        writer.visitField(ACC_PRIVATE | ACC_FINAL, "handle", 'L' + ownerInternal + ';', null, null);
        MethodVisitor mv = writer.visitMethod(ACC_PUBLIC, "<init>", "(L" + ownerInternal + ";)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, BRIDGE_INTERNAL, "<init>", "()V", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, executorName, "handle", 'L' + ownerInternal + ';');
        mv.visitInsn(RETURN);
        mv.visitEnd();
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        mv = writer.visitMethod(ACC_PUBLIC, "dispatch", "(Ljava/lang/Object;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, executorName, "handle", 'L' + ownerInternal + ';');
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, ownerInternal, methodName, "(L" + eventInternal + ";)V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        writer.visitEnd();
        val bytes = writer.toByteArray();
        val constructor = JavaUtil.unsafe().defineClass(executorName, bytes, 0, bytes.length, loader, null)
                .getDeclaredConstructor(Object.class);
        constructor.setAccessible(true);
        return (EventExecutor<?>) constructor.newInstance(listener);
    }

    private Class<?> createMagicBridge() {
        val writer = new ClassWriter(0);
        val version = JavaUtil.vmVersion();
        val prefix = (version > 8 ? "jdk/internal/" : "sun/") + "reflect/";
        val name = prefix + MAGIC_ACCESSOR_BRIDGE_CLASS_NAME;
        writer.visit(version + 44, ACC_PUBLIC, name, null, prefix + MAGIC_ACCESSOR_CLASS_NAME, null);
        val mv = writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, prefix + MAGIC_ACCESSOR_CLASS_NAME, "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        val bytes = writer.toByteArray();
        return JavaUtil.unsafe().defineClass(name, bytes, 0, bytes.length, null, null);
    }
}
