package ru.xdark.telekenetic.minecraft.transformers;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import ru.xdark.launcher.ClassFileTransformer;
import ru.xdark.launcher.ClassTransformation;
import ru.xdark.telekenetic.version.Version;

@RequiredArgsConstructor
public final class ClientBrandRetrieverTransformer implements ClassFileTransformer {

    private final Version loaderVersion;

    @Override
    public void transform(ClassTransformation transformation) throws Exception {
        if ("net.minecraft.client.ClientBrandRetriever".equals(transformation.getOriginalClassName())) {
            val reader = new ClassReader(transformation.getClassBytes());
            val writer = new ClassWriter(0);
            reader.accept(new ClassVisitor(Opcodes.ASM7, writer) {

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    val mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    if ("getClientModName".equals(name) && "()Ljava/lang/String;".equals(descriptor)) {
                        return new MethodVisitor(Opcodes.ASM7, mv) {

                            @Override
                            public void visitLdcInsn(Object value) {
                                super.visitLdcInsn("Telekinetic/" + loaderVersion);
                            }
                        };
                    }
                    return mv;
                }
            }, 0);
            transformation.setClassBytes(writer.toByteArray());
        }
    }
}
