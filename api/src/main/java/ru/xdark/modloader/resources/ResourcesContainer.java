package ru.xdark.modloader.resources;

import java.util.List;

public interface ResourcesContainer {

    List<Resource> findResources(String path);

    boolean hasResource(String path);
}
