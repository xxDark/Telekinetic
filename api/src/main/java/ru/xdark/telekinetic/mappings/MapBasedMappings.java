package ru.xdark.telekinetic.mappings;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public final class MapBasedMappings implements Mappings {
    private final Map<String, String> mappings;
    private final Map<String, String> reverted;
    private final Map<String, String> view;

    public MapBasedMappings(Map<String, String> mappings) {
        this.mappings = mappings;
        this.reverted = mappings.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        this.view = Collections.unmodifiableMap(mappings);
    }

    @Override
    public String map(String original) {
        return mappings.getOrDefault(original, original);
    }

    @Override
    public String unmap(String mapped) {
        return reverted.getOrDefault(mapped, mapped);
    }

    @Override
    public Map<String, String> asMap() {
        return view;
    }
}
