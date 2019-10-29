package ru.xdark.telekinetic.version;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class DefaultVersionTypes {

    private static final Map<String, VersionType> BY_NAME;
    public final VersionType UNKNOWN = new NamedVersionType("UNKNOWN", 0);
    public final VersionType ALPHA = new NamedVersionType("ALPHA", 100);
    public final VersionType BETA = new NamedVersionType("BETA", 200);
    public final VersionType SNAPSHOT = new NamedVersionType("SNAPSHOT", 300);
    public final VersionType RELEASE = new NamedVersionType("RELEASE", 400);

    public VersionType getByName(String name) {
        return BY_NAME.getOrDefault(name, UNKNOWN);
    }

    static {
        try {
            val fields = DefaultVersionTypes.class.getFields();
            int length = fields.length;
            val map = BY_NAME = new HashMap<>(length);
            while (length-->0) {
                val type = (VersionType) fields[length].get(null);
                BY_NAME.put(type.getId(), type);
            }
        } catch (IllegalAccessException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
}
