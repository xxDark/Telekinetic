package ru.xdark.modloader.resources;

import java.util.List;

public interface ResourceManager extends ResourcesContainer {

    List<Resource> findResources(Object mod, String location);
}
