package ru.xdark.launcher;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public final class ClassTransformation {

    private final String originalClassName;
    private final String transformedClassName;
    private final String untransformedClassName;
    private byte[] classBytes;

    public void setTransformationResult(byte[] transformationResult) {
        this.classBytes = transformationResult;
    }
}
