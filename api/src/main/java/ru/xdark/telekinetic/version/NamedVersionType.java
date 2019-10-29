package ru.xdark.telekinetic.version;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public final class NamedVersionType implements VersionType {
    private final String id;
    private final int priority;
}
