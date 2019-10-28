package ru.xdark.launcher;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

public interface NativeLibrariesAppender {
    void appendUrlToNativePath(URL url);

    void appendUriToNativePath(URI uri);

    void appendDirectoryToNativePath(Path directory);
}
