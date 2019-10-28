package ru.xdark.telekenetic.mod;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.xdark.telekenetic.version.Version;

@RequiredArgsConstructor
@Data
public final class ModDescription {
    private final String name;
    private final String id;
    private final Version version;
    private final String url;
    private final String[] authors;

    public static ModDescription fromAnnotation(Mod annotation) {
        return new ModDescription(
                annotation.name(),
                annotation.id(),
                Version.parse(annotation.version()),
                annotation.url(),
                annotation.authors()
        );
    }
}
