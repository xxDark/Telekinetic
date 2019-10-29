package ru.xdark.telekinetic.dependencies;

import ru.xdark.telekinetic.checksum.Checksum;
import ru.xdark.telekinetic.version.Version;

public interface Dependency {

    String getGroup();

    String getId();

    Version getVersion();

    Checksum getChecksum();
}
