package ru.xdark.telekenetic.resources;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.xdark.telekenetic.util.IOUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class UrlResource implements Resource {

    @NonNull
    private final URL url;

    @Override
    public String getFileName() {
        return url.getPath();
    }

    @Override
    public InputStream openStream() throws IOException {
        return url.openStream();
    }

    @Override
    public byte[] readAsBytes() throws IOException {
        try (val in = openStream()) {
            return IOUtil.toBytes(in);
        }
    }

    @Override
    public ByteBuffer readAsNioBuffer() throws IOException {
        val buffer = ByteBuffer.wrap(readAsBytes());
        buffer.clear();
        return buffer;
    }

    @Override
    public String readAsString() throws IOException {
        return readAsString(DEFAULT_CHARSET);
    }

    @Override
    public String readAsString(Charset charset) throws IOException {
        return new String(readAsBytes(), charset);
    }

    @Override
    public List<String> readAsLines() throws IOException {
        return readAsLines(DEFAULT_CHARSET);
    }

    @Override
    public List<String> readAsLines(Charset charset) throws IOException {
        try (val reader = new BufferedReader(new InputStreamReader(openStream(), charset))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    @Override
    public void copyToFile(Path path, OpenOption... options) throws IOException {
        try (val in = openStream()) {
            IOUtil.copyInputToFile(in, path, IOUtil.newThreadLocalBuffer(), options);
        }
    }

    @Override
    public void copyToDirectory(Path path, OpenOption... options) throws IOException {
        copyToFile(path.resolve(getFileName()), options);
    }
}
