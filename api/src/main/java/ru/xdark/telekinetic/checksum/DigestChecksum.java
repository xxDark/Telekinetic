package ru.xdark.telekinetic.checksum;

import lombok.RequiredArgsConstructor;
import ru.xdark.telekinetic.digest.Digest;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;

@RequiredArgsConstructor
public final class DigestChecksum implements Checksum {
    private final Digest digest;
    private final byte[] bytes;

    @Override
    public boolean verify(InputStream in, byte[] buffer) throws Exception {
        return Arrays.equals(bytes, digest.digest(in, buffer));
    }

    @Override
    public boolean verify(Path file, byte[] buffer) throws Exception {
        return Arrays.equals(bytes, digest.digest(file, buffer));
    }

    @Override
    public boolean verify(byte[] bytes) throws Exception {
        return Arrays.equals(bytes, digest.digest(bytes));
    }
}
