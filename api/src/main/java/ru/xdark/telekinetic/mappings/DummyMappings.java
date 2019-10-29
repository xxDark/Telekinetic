package ru.xdark.telekinetic.mappings;

import java.util.Collections;
import java.util.Map;

public class DummyMappings implements Mappings {
    @Override
    public final String map(String original) {
        return original;
    }

    @Override
    public final String unmap(String mapped) {
        return mapped;
    }

    @Override
    public final Map<String, String> asMap() {
        return Collections.emptyMap();
    }
}
