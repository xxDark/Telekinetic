package ru.xdark.telekinetic.version;

import ru.xdark.telekinetic.Identifiable;

public interface VersionType extends Identifiable<String>, Comparable<VersionType> {

    int getPriority();

    @Override
    default int compareTo(VersionType o) {
        return Integer.compare(getPriority(), o.getPriority());
    }
}
