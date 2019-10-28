package ru.xdark.launcher;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.Predicate;

public interface ClasspathAppender {
    void appendUrlToClassPath(URL url);

    void appendUriToClassPath(URI uri);

    void appendDirectoryToClassPath(Path directory, Predicate<Path> filter);
}
