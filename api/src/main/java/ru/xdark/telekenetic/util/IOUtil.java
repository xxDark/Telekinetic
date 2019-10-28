package ru.xdark.telekenetic.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import ru.xdark.telekenetic.io.MoreOpenOption;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Arrays;

@UtilityClass
public class IOUtil {

    private final int BUFFER_SIZE = Integer.getInteger("modloader.buffer-size", 1024);
    private final ThreadLocal<byte[]> THREAD_LOCAL_BUFFER = ThreadLocal.withInitial(() -> new byte[BUFFER_SIZE]);

    public void transferInto(InputStream in, OutputStream out, byte[] buffer) throws IOException {
        for (int r = in.read(buffer); r != -1; r = in.read(buffer)) {
            out.write(buffer, 0, r);
        }
    }

    public void transferInto(InputStream in, OutputStream out) throws IOException {
        transferInto(in, out, newThreadLocalBuffer());
    }

    public byte[] toBytes(InputStream in, int optionalLength, byte[] buffer) throws IOException {
        val out = new ByteArrayOutputStream(optionalLength);
        transferInto(in, out, buffer);
        return out.toByteArray();
    }

    public byte[] toBytes(InputStream in, byte[] buffer) throws IOException {
        return toBytes(in, in.available(), buffer);
    }

    public byte[] toBytes(InputStream in, int optionalLength) throws IOException {
        return toBytes(in, optionalLength, newThreadLocalBuffer());
    }

    public byte[] toBytes(InputStream in) throws IOException {
        return toBytes(in, newThreadLocalBuffer());
    }

    public int normalizeInputSize(InputStream in) throws IOException {
        val available = in.available();
        return Math.max(available, 0);
    }

    public void createDirectoryIfNotExists(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            Files.createDirectories(path);
        }
    }

    public void copyInputToFile(InputStream in, Path destination, byte[] buffer, OpenOption... options) throws IOException {
        val view = Arrays.asList(options);
        if (view.contains(MoreOpenOption.OVERWRITE) && Files.exists(destination)) return;
        if (view.contains(MoreOpenOption.NO_PARENT_CHECK)) {
            createDirectoryIfNotExists(destination.getParent());
        }
        try (val out = openOutputStream(destination, options)) {
            transferInto(in, out, buffer);
        }
    }

    public void copyFile(Path source, Path destination, byte[] buffer, OpenOption... options) throws IOException {
        val view = Arrays.asList(options);
        if (view.contains(MoreOpenOption.OVERWRITE) && Files.exists(destination)) return;
        if (view.contains(MoreOpenOption.NO_PARENT_CHECK)) {
            createDirectoryIfNotExists(destination.getParent());
        }
        try (val in = openInputStream(source); val out = openOutputStream(destination, options)) {
            transferInto(in, out, buffer);
        }
    }

    public void copyFile(Path source, Path destination, OpenOption... options) throws IOException {
        copyFile(source, destination, newThreadLocalBuffer(), options);
    }

    public InputStream openInputStream(Path path, OpenOption... options) throws IOException {
        return Files.newInputStream(path, options);
    }

    public OutputStream openOutputStream(Path path, OpenOption... options) throws IOException {
        return Files.newOutputStream(path, options);
    }

    public boolean hasExtension(Path path, String extension) {
        return path.getFileName().toString().endsWith('.' + extension);
    }

    public boolean hasExtensions(Path path, String... extensions) {
        val fileName = path.getFileName().toString();
        return Arrays.stream(extensions).map("."::concat).anyMatch(fileName::endsWith);
    }

    public byte[] newThreadLocalBuffer() {
        return THREAD_LOCAL_BUFFER.get();
    }

    public byte[] newBuffer() {
        return new byte[BUFFER_SIZE];
    }
}
