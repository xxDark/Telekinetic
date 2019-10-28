package ru.xdark.telekenetic.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;

public interface Resource {

    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    String getFileName();

    InputStream openStream() throws IOException;

    byte[] readAsBytes() throws IOException;

    ByteBuffer readAsNioBuffer() throws IOException;

    String readAsString() throws IOException;

    String readAsString(Charset charset) throws IOException;

    List<String> readAsLines() throws IOException;

    List<String> readAsLines(Charset charset) throws IOException;

    void copyToFile(Path path, OpenOption... options) throws IOException;

    void copyToDirectory(Path path, OpenOption... options) throws IOException;
}
