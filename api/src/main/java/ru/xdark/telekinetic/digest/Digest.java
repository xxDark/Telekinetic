package ru.xdark.telekinetic.digest;

import ru.xdark.telekinetic.Identifiable;

import java.io.InputStream;
import java.nio.file.Path;

public interface Digest extends Identifiable<String> {

    byte[] digest(InputStream in, byte[] buffer) throws Exception;

    byte[] digest(Path file, byte[] buffer) throws Exception;

    byte[] digest(byte[] bytes) throws Exception;
}
