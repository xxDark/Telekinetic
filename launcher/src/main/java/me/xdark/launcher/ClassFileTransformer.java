package me.xdark.launcher;

@FunctionalInterface
public interface ClassFileTransformer {
    void transform(ClassTransformation transformation) throws Exception;
}
