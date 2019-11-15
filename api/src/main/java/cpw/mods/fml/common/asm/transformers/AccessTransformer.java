package cpw.mods.fml.common.asm.transformers;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.xdark.launcher.ClassFileTransformer;
import ru.xdark.launcher.ClassTransformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import static org.objectweb.asm.Opcodes.*;

@Log4j2
public final class AccessTransformer implements ClassFileTransformer {

    private static class Modifier {
        String name = "";
        String desc = "";
        int oldAccess = 0;
        int newAccess = 0;
        int targetAccess = 0;
        boolean changeFinal = false;
        boolean markFinal = false;
        boolean modifyClassVisibility;

        private void setTargetAccess(String name) {
            if (name.startsWith("public")) targetAccess = ACC_PUBLIC;
            else if (name.startsWith("private")) targetAccess = ACC_PRIVATE;
            else if (name.startsWith("protected")) targetAccess = ACC_PROTECTED;

            if (name.endsWith("-f")) {
                changeFinal = true;
                markFinal = false;
            } else if (name.endsWith("+f")) {
                changeFinal = true;
                markFinal = true;
            }
        }
    }

    private final Map<String, List<Modifier>> modifiers = new HashMap<>();

    public AccessTransformer(JarFile jar, String atList) throws IOException {
        for (val at : atList.split(" ")) {
            val jarEntry = jar.getEntry("META-INF/" + at);
            if (jarEntry != null) {
                try (InputStream in = jar.getInputStream(jarEntry);
                     BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    processATFile(br);
                }
            } else {
                log.warn("Unknown entry file: {}", at);
            }
        }
        log.info("Loaded {} rules from AccessTransformer mod jar file {}", modifiers.size(), jar.getName());
    }

    public AccessTransformer(String... lines) {
        for (val line : lines) {
            processLine(line);
        }
        log.info("Loaded {} rules from AccessTransformer lines", modifiers.size());
    }

    private void processATFile(BufferedReader reader) throws IOException {
        while (reader.ready()) {
            processLine(reader.readLine());
        }
    }

    private void processLine(String input) {
        val spl = Arrays.asList(input.split("#")).iterator();
        val line = spl.hasNext() ? spl.next().trim() : "";
        if (line.isEmpty()) {
            return;
        }
        val parts = Arrays.asList(line.split(" "));
        if (parts.size() > 3) {
            throw new RuntimeException("Invalid config file line " + input);
        }
        val m = new Modifier();
        m.setTargetAccess(parts.get(0));

        if (parts.size() == 2) {
            m.modifyClassVisibility = true;
        } else {
            val nameReference = parts.get(2);
            int parenIdx = nameReference.indexOf('(');
            if (parenIdx > 0) {
                m.desc = nameReference.substring(parenIdx);
                m.name = nameReference.substring(0, parenIdx);
            } else {
                m.name = nameReference;
            }
        }
        val className = parts.get(1).replace('/', '.');
        modifiers.computeIfAbsent(className, k -> new ArrayList<>(4)).add(m);
    }

    @Override
    public void transform(ClassTransformation transformation) throws Exception {
        val bytes = transformation.getClassBytes();
        if (bytes == null) return;
        val transformedName = transformation.getTransformedClassName();
        val modifiers = this.modifiers.get(transformedName);
        if (modifiers == null) return;
        val cn = new ClassNode();
        new ClassReader(bytes).accept(cn, 0);

        for (val m : modifiers) {
            if (m.modifyClassVisibility) {
                cn.access = getFixedAccess(cn.access, m);
                continue;
            }
            if (m.desc.isEmpty()) {
                for (val n : (List<FieldNode>) cn.fields) {
                    if (n.name.equals(m.name) || m.name.equals("*")) {
                        n.access = getFixedAccess(n.access, m);
                        if (!m.name.equals("*")) {
                            break;
                        }
                    }
                }
            } else {
                val nowOverridable = new ArrayList<MethodNode>(4);
                for (val n : (List<MethodNode>) cn.methods) {
                    if ((n.name.equals(m.name) && n.desc.equals(m.desc)) || m.name.equals("*")) {
                        n.access = getFixedAccess(n.access, m);

                        if (!n.name.equals("<init>")) {
                            val wasPrivate = (m.oldAccess & ACC_PRIVATE) == ACC_PRIVATE;
                            val isNowPrivate = (m.newAccess & ACC_PRIVATE) == ACC_PRIVATE;
                            if (wasPrivate && !isNowPrivate) {
                                nowOverridable.add(n);
                            }
                        }

                        if (!m.name.equals("*")) {
                            break;
                        }
                    }
                }

                replaceInvokeSpecial(cn, nowOverridable);
            }
        }

        val cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        transformation.setTransformationResult(cw.toByteArray());
    }

    private void replaceInvokeSpecial(ClassNode clazz, List<MethodNode> toReplace) {
        for (val method : (List<MethodNode>) clazz.methods) {
            for (Iterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext(); ) {
                val insn = it.next();
                if (insn.getOpcode() == INVOKESPECIAL) {
                    val mInsn = (MethodInsnNode) insn;
                    for (int i = 0, toReplaceSize = toReplace.size(); i < toReplaceSize; i++) {
                        val n = toReplace.get(i);
                        if (n.name.equals(mInsn.name) && n.desc.equals(mInsn.desc)) {
                            mInsn.setOpcode(INVOKEVIRTUAL);
                            break;
                        }
                    }
                }
            }
        }
    }

    private int getFixedAccess(int access, Modifier target) {
        target.oldAccess = access;
        int t = target.targetAccess;
        int ret = (access & ~7);

        switch (access & 7) {
            case ACC_PRIVATE:
                ret |= t;
                break;
            case 0: // default
                ret |= (t != ACC_PRIVATE ? t : 0 /* default */);
                break;
            case ACC_PROTECTED:
                ret |= (t != ACC_PRIVATE && t != 0 /* default */ ? t : ACC_PROTECTED);
                break;
            case ACC_PUBLIC:
                ret |= (t != ACC_PRIVATE && t != 0 /* default */ && t != ACC_PROTECTED ? t : ACC_PUBLIC);
                break;
            default:
                throw new RuntimeException("The fuck?");
        }

        // Clear the "final" marker on fields only if specified in control field
        if (target.changeFinal) {
            if (target.markFinal) {
                ret |= ACC_FINAL;
            } else {
                ret &= ~ACC_FINAL;
            }
        }
        target.newAccess = ret;
        return ret;
    }
}