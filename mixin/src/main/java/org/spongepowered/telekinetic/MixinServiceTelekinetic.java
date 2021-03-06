package org.spongepowered.telekinetic;

import lombok.Getter;
import lombok.val;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.mixin.transformer.TelekineticProxy;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IClassTracker;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.service.MixinServiceAbstract;
import ru.xdark.launcher.ClassLoadingController;
import ru.xdark.launcher.ClassTransformation;
import ru.xdark.launcher.RootLauncher;
import ru.xdark.telekinetic.util.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public final class MixinServiceTelekinetic extends MixinServiceAbstract implements IClassProvider, IClassBytecodeProvider, ITransformerProvider, IClassTracker {

    private final ClassLoadingController controller;
    @Getter private final IMixinAuditTrail auditTrail;

    public MixinServiceTelekinetic() { // I hate ServiceLoader
        // Use RootLauncher as class loading controller
        this.controller = RootLauncher.get();
        this.auditTrail = new TelekineticAuditTail();
    }

    @Override
    public ClassNode getClassNode(String name) throws IOException {
        return getClassNode(name, false);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) throws IOException {
        val transformedName = name.replace('/', '.');
        val controller = this.controller;
        val untransformClassName = controller.untransformClassName(transformedName);
        URL resource = controller.findClassResource(transformedName);
        if (resource == null) {
            resource = controller.findClassResource(untransformClassName);
        }
        if (resource == null) return null;
        byte[] classBytes;
        try (val in = resource.openStream()) {
            classBytes = IOUtil.toBytes(in);
        }
        if (runTransformers) {
            val transformation = new ClassTransformation(name, controller.transformClassName(name), untransformClassName, classBytes);
            val newBytes = controller.runTransformation(transformation).getClassBytes();
            if (newBytes == null) {
                throw new IllegalStateException("Transformation did not return valid class bytes!");
            }
            classBytes = newBytes;
        }
        val node = new ClassNode();
        new ClassReader(classBytes).accept(node, 0);
        return node;
    }

    @Override
    public URL[] getClassPath() {
        return this.controller.getClassPath();
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return this.controller.findClass(name);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return this.controller.findClass(name, initialize);
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return this.controller.findClass(name, initialize);
    }

    @Override
    public String getName() {
        return "Telekinetic";
    }

    @Override
    public boolean isValid() {
        try {
            Class.forName("ru.xdark.launcher.ClassLoadingController");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    @Override
    public IClassProvider getClassProvider() {
        return this;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return this;
    }

    @Override
    public IClassTracker getClassTracker() {
        return this;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return Collections.singletonList(
                "org.spongepowered.telekinetic.MixinPlatformAgentTelekinetic"
        );
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        try {
            return new ContainerHandleURI(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return this.controller.getResourceAsStream(name);
    }

    @Override
    public Collection<ITransformer> getTransformers() {
        return this.controller.getTransformers().stream().map(TelekineticTransformerWrapper::new).collect(Collectors.toList());
    }

    @Override
    public Collection<ITransformer> getDelegatedTransformers() {
        return Collections.emptyList();
    }

    @Override
    public void addTransformerExclusion(String name) {
        controller.addTransformerExclusion(name);
    }

    @Override
    public void registerInvalidClass(String className) {
        // Do nothing
    }

    @Override
    public boolean isClassLoaded(String className) {
        return controller.isClassLoaded(className);
    }

    @Override
    public String getClassRestrictions(String className) {
        String restrictions = "";
        if (this.controller.isClassLoadingExclusion(className)) {
            restrictions = "PACKAGE_CLASSLOADER_EXCLUSION";
        }
        if (this.controller.isTransformationExclusion(className)) {
            restrictions = (restrictions.length() > 0 ? restrictions + "," : "") + "PACKAGE_TRANSFORMER_EXCLUSION";
        }
        return restrictions;
    }

    @Override
    public void beginPhase() {
        controller.registerTransformer(new TelekineticProxy());
    }
}
