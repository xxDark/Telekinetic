package ru.xdark.launcher;

public interface ClassNameTransformer {

    String transform(String untransformedName);

    String untransform(String transformedName);
}
