package me.xdark.launcher;

public interface ClassTransformation {

    String getClassName();

    byte[] getClassBytes();

    void setTransformationResult(byte[] transformationResult);
}
