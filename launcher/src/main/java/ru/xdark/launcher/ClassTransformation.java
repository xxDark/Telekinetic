package ru.xdark.launcher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@AllArgsConstructor
@Data
public final class ClassTransformation {

    private final String originalClassName;
    private final String transformedClassName;
    private final String untransformedClassName;
    @Setter(AccessLevel.NONE)
    private byte[] classBytes;

    public void setTransformationResult(byte[] transformationResult) {
        this.classBytes = transformationResult;
    }
}
