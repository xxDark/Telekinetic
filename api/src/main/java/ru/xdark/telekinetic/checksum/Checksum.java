package ru.xdark.telekinetic.checksum;

import java.io.InputStream;
import java.nio.file.Path;

public interface Checksum {
    boolean verify(InputStream in, byte[] buffer) throws Exception;

    boolean verify(Path file, byte[] buffer) throws Exception;

    boolean verify(byte[] bytes) throws Exception;
}
