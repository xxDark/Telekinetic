package ru.xdark.telekinetic.mappings;

import java.util.Map;

public interface Mappings {

    String map(String original);

    String unmap(String mapped);

    Map<String, String> asMap();
}
