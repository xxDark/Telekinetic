package ru.xdark.telekinetic.resources;

import java.util.List;

public interface ResourceManager extends ResourcesContainer {

    List<Resource> findResources(Object mod, String location);

    boolean hasResource(Object mod, String location);
}