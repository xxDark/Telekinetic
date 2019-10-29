package ru.xdark.telekinetic.digest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@RequiredArgsConstructor
public final class MessageDigest implements Digest {

    @Getter private final String id;

    @Override
    public byte[] digest(InputStream in, byte[] buffer) throws Exception {
        val md = getInstance();
        for (int r = in.read(buffer);r != -1; r = in.read(buffer)) {
            md.update(buffer, 0, r);
        }
        return md.digest();
    }

    @Override
    public byte[] digest(Path file, byte[] buffer) throws Exception {
        try (val in = Files.newInputStream(file, StandardOpenOption.READ)) {
            return digest(in, buffer);
        }
    }

    @Override
    public byte[] digest(byte[] bytes) throws Exception {
        val md = getInstance();
        md.update(bytes);
        return md.digest();
    }

    @SneakyThrows
    private java.security.MessageDigest getInstance() {
        return java.security.MessageDigest.getInstance(id);
    }
}
