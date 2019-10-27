package ru.xdark.launcher;

import joptsimple.ValueConverter;

import java.nio.file.Path;
import java.nio.file.Paths;

final class PathValueConverter implements ValueConverter<Path> {
    @Override
    public Path convert(String value) {
        return Paths.get(value);
    }

    @Override
    public Class<? extends Path> valueType() {
        return Path.class;
    }

    @Override
    public String valuePattern() {
        return null;
    }
}
